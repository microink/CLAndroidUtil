package com.microink.clandroid.android.http;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.microink.clandroid.android.log.PrintLineLog;
import com.microink.clandroid.java.exception.ExceptionUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/9 2:46 PM
 *
 * OkHttp工具类
 */
public class OkHttpUtil {

    // 提交数据Media类型 json
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    public static final String MEDIA_TYPE_IMAGE_JPEG = "image/jpeg";

    public static final int ON_FAILED_CODE = -1; // 失败时返回的code，用于区分是解析失败还是http连接失败
    public static int CONNECT_TIME = 20000; // 连接超时时间

    private static volatile OkHttpUtil sInstance;

    private OkHttpClient okHttpClient;
    private Handler mainHandler;

    private OkHttpUtil() {
        mainHandler = new Handler(Looper.getMainLooper());
        initOkHttpClient();
    }

    public static OkHttpUtil getInstance() {
        if (null == sInstance) {
            synchronized (OkHttpUtil.class) {
                if (null == sInstance) {
                    sInstance = new OkHttpUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化OkHttpClient
     */
    private void initOkHttpClient() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (Build.VERSION.SDK_INT < 28) {
                builder.sslSocketFactory(sslSocketFactory);
            } else {
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            }
            //builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            okHttpClient = builder.readTimeout(CONNECT_TIME, TimeUnit.MILLISECONDS)
                    .connectTimeout(CONNECT_TIME, TimeUnit.MILLISECONDS).hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取OkHttpClient
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 在主线程执行
     * @param runnable
     */
    public void executeFunInMain(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * 根据url和参数请求接口
     * @param url
     * @return
     */
    public static <T> Call pullJsonRequestData(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final ResponseCallBack<T> callback){

        Request request = getJsonRequest(url, headers, paramsMap);

        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pullJson start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pullJson url= " + url);
                PrintLineLog.v("pullJson headers= " + getMapString(headers));
                PrintLineLog.i("pullJson params= " + getMapString(paramsMap));
                PrintLineLog.e("pullJson onFailure useTime= " + useTime);
                PrintLineLog.e(e);
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, null, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pullJsonRequestData url= " + url);
                PrintLineLog.v("pullJsonRequestData headers= " + getMapString(headers));
                PrintLineLog.i("pullJsonRequestData params= " + getMapString(paramsMap));
                PrintLineLog.i("pullJsonRequestData onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                    callback.parseNetworkResponseStr(code, responseStr, useTime, call);
                } catch (final Exception e) {
                    final String finalResponseStr = responseStr;
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, finalResponseStr, useTime, call);
                        }
                    });
                    return;
                }
            }
        });
        return call;
    }

    /**
     * 根据Header集合获取Request.Builder
     * @param headers
     * @return
     */
    private static Request.Builder getRequestBuilderWithHeader(Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        if (null != headers) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        return builder;
    }

    /**
     * 获取Json的请求Request
     * @return
     */
    public static Request getJsonRequest(String url, Map<String, String> headers,
            Map<String, String> paramsMap) {
        // 构造参数string
        JSONObject json = new JSONObject();
        for (String key : paramsMap.keySet()) {
            try {
                json.put(key, paramsMap.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MediaType mediaType = MediaType.parse(OkHttpUtil.MEDIA_TYPE_JSON);
        String params = json.toString();
        RequestBody requestBody = RequestBody.create(params, mediaType);
        Request.Builder builder = getRequestBuilderWithHeader(headers);
        Request request = builder.url(url)
                .post(requestBody)
                .build();
        return request;
    }

    /**
     * 获取From的请求Request
     * @return
     */
    private static Request getFormRequest(String url, Map<String, String> headers,
            Map<String, String> paramsMap) {
        // 构造参数
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            formBuilder.add(key, paramsMap.get(key));
        }

        FormBody formBody = formBuilder.build();
        Request.Builder builder = getRequestBuilderWithHeader(headers);
        Request request = builder.url(url)
                .post(formBody)
                .build();
        return request;
    }

    /**
     * 根据url和参数请求接口，用于上传文件
     * @param url
     * @return
     */
    private static <T> Call pullFileRequestData(String url, Map<String, String> headers,
            Map<String, String> paramsMap, Map<String, RequestFileBean> paramsFileMap,
            final ResponseCallBack<T> callback){

        Request request = getUploadFileRequest(url, headers, paramsMap, paramsFileMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, null, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                    PrintLineLog.i("pull onResponse result= " + responseStr);
                    callback.parseNetworkResponseStr(code, responseStr, useTime, call);
                } catch (final Exception e) {
                    final String finalResponseStr = responseStr;
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, finalResponseStr, useTime, call);
                        }
                    });
                    return;
                }
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口 上传文件 返回String
     * @param url
     * @param callback
     * @throws JSONException
     */
    private static Call pullFileRequestStringData(String url, Map<String, String> headers,
            Map<String, String> paramsMap, Map<String, RequestFileBean> paramsFileMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getUploadFileRequest(url, headers, paramsMap, paramsFileMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, useTime, null);
                        }
                    });
                    return;
                }
                final String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(code, finalResponseStr, useTime, call);
                    }
                });
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口 上传文件 返回String
     * 异步回调
     * @param url
     * @param callback
     * @throws JSONException
     */
    private static Call pullFileRequestStringDataAsync(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap, Map<String, RequestFileBean> paramsFileMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getUploadFileRequest(url, headers, paramsMap, paramsFileMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pullFile start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pullFile url= " + url);
                PrintLineLog.v("pullFile headers= " + getMapString(headers));
                PrintLineLog.i("pullFile params= " + getMapString(paramsMap));
                PrintLineLog.e("pullFile onFailure useTime= " + useTime);
                PrintLineLog.e("pullFile onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                callback.onFailed(ON_FAILED_CODE, e, useTime, call);
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pullFile url= " + url);
                PrintLineLog.v("pullFile headers= " + getMapString(headers));
                PrintLineLog.i("pullFile params= " + getMapString(paramsMap));
                PrintLineLog.i("pullFile onFailure useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    callback.onFailed(code, e, useTime, null);
                    return;
                }
                final String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                callback.onResponse(code, finalResponseStr, useTime, call);
            }
        });
        return call;
    }

    /**
     * 获取上传文件的Request
     * @param url
     * @param paramsFileMap
     * @return
     */
    private static Request getUploadFileRequest(String url, Map<String, String> headers,
            Map<String, String> paramsMap, Map<String, RequestFileBean> paramsFileMap) {
        PrintLineLog.d("getUploadFileRequest start url= " + url);
        //PrintLineLog.d("getUploadFileRequest headers= " + getMapString(headers));
        MultipartBody.Builder multipartBodyBuild = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        // 构造参数
        for (String key : paramsMap.keySet()) {
            multipartBodyBuild.addFormDataPart(key, paramsMap.get(key));
        }
        File uploadFile = null;
        RequestFileBean fileBean = null;
        for (String key : paramsFileMap.keySet()) {
            RequestFileBean requestFileBean = paramsFileMap.get(key);
            String filePath = requestFileBean.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                uploadFile = new File(filePath);
                fileBean = requestFileBean;
                PrintLineLog.d("getUploadFileRequest uploadFile= " +
                        uploadFile.getAbsolutePath());
            } else {
                multipartBodyBuild.addFormDataPart(requestFileBean.getName(),
                        requestFileBean.getFileName());
                PrintLineLog.d("getUploadFileRequest addFormDataPart= " +
                        requestFileBean.getName() + ":" + requestFileBean.getFileName());
            }
        }
        if (null != fileBean) {
            MediaType mediaType = MediaType.parse(OkHttpUtil.MEDIA_TYPE_IMAGE_JPEG);
            RequestBody fileBody = RequestBody.create(uploadFile, mediaType);
            multipartBodyBuild.addFormDataPart(fileBean.getName(), fileBean.getFileName(), fileBody);
        }

        RequestBody requestBody = multipartBodyBuild
                .build();
        Request.Builder builder = getRequestBuilderWithHeader(headers);
        Request request = builder.url(url)
                .post(requestBody)
                .build();

        return request;
    }

    /**
     * 根据url和参数请求接口 返回String
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullJsonRequestStringData(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getJsonRequest(url, headers, paramsMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, useTime, null);
                        }
                    });
                    return;
                }
                final String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(code, finalResponseStr, useTime, call);
                    }
                });
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口 返回String
     * 异步回调
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullJsonRequestStringDataAsync(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getJsonRequest(url, headers, paramsMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                callback.onFailed(ON_FAILED_CODE, e, useTime, call);
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    callback.onFailed(code, e, useTime, null);
                    return;
                }
                String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                callback.onResponse(code, finalResponseStr, useTime, call);
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求表单接口
     * @param url
     * @return
     */
    private static <T> Call pullFormRequestData(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final ResponseCallBack<T> callback){

        Request request = getFormRequest(url, headers, paramsMap);
        PrintLineLog.d("pull start url= " + url);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, null, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                    PrintLineLog.i("pull onResponse result= " + responseStr);
                    callback.parseNetworkResponseStr(code, responseStr, useTime, call);
                } catch (final Exception e) {
                    final String finalResponseStr = responseStr;
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, finalResponseStr, useTime, call);
                        }
                    });
                    return;
                }
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口 返回String
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullFormRequestStringData(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getFormRequest(url, headers, paramsMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, useTime, null);
                        }
                    });
                    return;
                }
                final String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(code, finalResponseStr, useTime, call);
                    }
                });
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口 返回String
     * 异步回调
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullFormRequestStringDataAsync(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap,
            final OkHttpUtilStringCallback callback) {

        Request request = getFormRequest(url, headers, paramsMap);

        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                callback.onFailed(ON_FAILED_CODE, e, useTime, call);
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                String responseStr = "";
                final int code = response.code();
                try {
                    responseStr = response.body().string();
                } catch (final Exception e) {
                    PrintLineLog.e("pull onResponse Exception code= " + code + " Exception=" +
                            ExceptionUtil.exceptionToString(e));
                    callback.onFailed(code, e, useTime, null);
                    return;
                }
                String finalResponseStr = responseStr;
                PrintLineLog.i("pull onResponse result= " + responseStr);
                callback.onResponse(code, finalResponseStr, useTime, call);
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口下载文件
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullDownloadFile(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap, final File file,
            final OkHttpUtilStringCallback callback) {

        if (TextUtils.isEmpty(url) || null == file || null == callback) {
            return null;
        }
        Request request = getJsonRequest(url, headers, paramsMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull filePath= " + file.getAbsolutePath());
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(ON_FAILED_CODE, e, useTime, call);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull filePath= " + file.getAbsolutePath());
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                final int code = response.code();
                BufferedSink bufferedSink = null;

                File parentFile = file.getParentFile();
                if (null != parentFile) {
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                }
                try {
                    Sink sink = Okio.sink(file);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                } catch (final Exception e) {
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, useTime, null);
                        }
                    });
                    return;
                } finally {
                    if (null != bufferedSink){
                        bufferedSink.close();
                    }
                }
                OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(code, "success", useTime, call);
                    }
                });
            }
        });
        return call;
    }

    /**
     * 根据url和参数请求接口下载文件
     * 异步回调
     * @param url
     * @param paramsMap
     * @param callback
     * @throws JSONException
     */
    private static Call pullDownloadFileAsync(final String url, final Map<String, String> headers,
            final Map<String, String> paramsMap, final File file,
            final OkHttpUtilStringCallback callback) {

        if (TextUtils.isEmpty(url) || null == file || null == callback) {
            return null;
        }
        Request request = getJsonRequest(url, headers, paramsMap);
        final long startTime = System.currentTimeMillis();
        Call call = OkHttpUtil.getInstance().getOkHttpClient()
                .newCall(request);
        PrintLineLog.d("pull start url= " + url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull filePath= " + file.getAbsolutePath());
                PrintLineLog.e("pull onFailure useTime= " + useTime);
                PrintLineLog.e("pull onFailure Exception= " +
                        ExceptionUtil.exceptionToString(e));
                callback.onFailed(ON_FAILED_CODE, e, useTime, call);
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                final long useTime = System.currentTimeMillis() - startTime;
                PrintLineLog.i("pull url= " + url);
                PrintLineLog.v("pull headers= " + getMapString(headers));
                PrintLineLog.i("pull params= " + getMapString(paramsMap));
                PrintLineLog.i("pull filePath= " + file.getAbsolutePath());
                PrintLineLog.i("pull onResponse useTime= " + useTime);
                final int code = response.code();
                BufferedSink bufferedSink = null;

                File parentFile = file.getParentFile();
                if (null != parentFile) {
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                }
                try {
                    Sink sink = Okio.sink(file);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                } catch (final Exception e) {
                    OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(code, e, useTime, null);
                        }
                    });
                    return;
                } finally {
                    if (null != bufferedSink){
                        bufferedSink.close();
                    }
                }
                callback.onResponse(code, "success", useTime, call);
            }
        });
        return call;
    }

    /**
     * 获取Map的字符串
     * @param map
     * @return
     */
    public static String getMapString(Map<String, String> map) {
        if (null == map) {
            return "";
        }
        JSONObject json = new JSONObject();
        for (String key : map.keySet()) {
            try {
                json.put(key, map.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }

    /**
     * 请求构造类
     */
    public static class OkHttpUtilBuilder {
        protected Map<String, String> paramsMap;
        protected Map<String, String> headersMap;
        protected String url;
        protected Call call;
        public OkHttpUtilBuilder() {
            paramsMap = new IdentityHashMap<>();
        }
        public OkHttpUtilBuilder addParams(String key, String params) {
            paramsMap.put(key, params);
            return this;
        }

        public OkHttpUtilBuilder setHeaders(Map<String, String> headers) {
            this.headersMap = headers;
            return this;
        }

        public OkHttpUtilBuilder addHeader(String name, String value) {
            if (null == headersMap) {
                headersMap = new HashMap<>();
            }
            headersMap.put(name, value);
            return this;
        }

        /**
         * 获取参数的String形式
         * @param builder
         * @return
         */
        public static String getParamsString(OkHttpUtilBuilder builder) {
            if (null == builder) {
                return "";
            }
            JSONObject json = new JSONObject();
            for (String key : builder.paramsMap.keySet()) {
                try {
                    json.put(key, builder.paramsMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }

        public static OkHttpUtilBuilder requestUlr(String url) {
            OkHttpUtilBuilder okHttpUtilBuilder = new OkHttpUtilBuilder();
            okHttpUtilBuilder.url = url;
            return okHttpUtilBuilder;
        }

        /**
         * 取消请求
         */
        public void cancelRequest() {
            if (null != call) {
                PrintLineLog.i("cancelRequest");
                call.cancel();
            }
        }

        /**
         * 获取Json结果
         * @param callback
         * @throws JSONException
         */
        public <T> void postJsonRequestCallObject(ResponseCallBack<T> callback) {
            call = OkHttpUtil.pullJsonRequestData(url, headersMap, paramsMap, callback);
        }

        /**
         * 获取字符串结果
         * @param callback
         */
        public void postJsonRequestCallString(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullJsonRequestStringData(url, headersMap, paramsMap, callback);
        }

        /**
         * 获取字符串结果异步回调
         * @param callback
         */
        public void postJsonRequestCallStringAsync(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullJsonRequestStringDataAsync(url, headersMap, paramsMap, callback);
        }
    }

    /**
     * 表单请求构造类
     */
    public static class OkHttpUtilFormBuilder {
        protected Map<String, String> paramsMap;
        protected Map<String, String> headersMap;
        protected String url;
        protected Call call;
        public OkHttpUtilFormBuilder() {
            paramsMap = new IdentityHashMap<>();
        }
        public OkHttpUtilFormBuilder addParams(String key, String params) {
            paramsMap.put(key, params);
            return this;
        }

        public OkHttpUtilFormBuilder setHeaders(Map<String, String> headers) {
            this.headersMap = headers;
            return this;
        }

        public OkHttpUtilFormBuilder addHeader(String name, String value) {
            if (null == headersMap) {
                headersMap = new HashMap<>();
            }
            headersMap.put(name, value);
            return this;
        }

        /**
         * 获取参数的String形式
         * @param builder
         * @return
         */
        public static String getParamsString(OkHttpUtilFormBuilder builder) {
            if (null == builder) {
                return "";
            }
            JSONObject json = new JSONObject();
            for (String key : builder.paramsMap.keySet()) {
                try {
                    json.put(key, builder.paramsMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }

        public static OkHttpUtilFormBuilder requestUlr(String url) {
            OkHttpUtilFormBuilder okHttpUtilBuilder = new OkHttpUtilFormBuilder();
            okHttpUtilBuilder.url = url;
            return okHttpUtilBuilder;
        }

        /**
         * 取消请求
         */
        public void cancelRequest() {
            if (null != call) {
                PrintLineLog.i("cancelRequest");
                call.cancel();
            }
        }

        /**
         * 获取Json结果
         * @param callback
         * @throws JSONException
         */
        public <T> void postFormRequestCallObject(ResponseCallBack<T> callback) {
            call = OkHttpUtil.pullFormRequestData(url, headersMap, paramsMap, callback);
        }

        /**
         * 获取字符串结果
         * @param callback
         */
        public void postFormRequestCallString(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullFormRequestStringData(url, headersMap, paramsMap, callback);
        }

        /**
         * 获取字符串结果 异步回调
         * @param callback
         */
        public void postFormRequestCallStringAsync(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullFormRequestStringDataAsync(url, headersMap, paramsMap, callback);
        }
    }

    /**
     * 请求构造类
     */
    public static class OkHttpUtilFileBuilder{
        protected Map<String, String> paramsMap;
        protected Map<String, RequestFileBean> partFileMap;
        protected Map<String, String> headersMap;
        protected String url;
        protected Call call;

        public OkHttpUtilFileBuilder() {
            super();
            paramsMap = new IdentityHashMap<>();
            partFileMap = new IdentityHashMap<>();
        }

        public OkHttpUtilFileBuilder addParams(String key, String params) {
            paramsMap.put(key, params);
            return this;
        }

        public OkHttpUtilFileBuilder addFormDataPart(String name, String fileName, File file) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(fileName) || null == file) {
                Log.e("OkHttpUtilFileBuilder", "addFormDataPart() params must not be null");
                return this;
            }
            RequestFileBean fileBean = new RequestFileBean(name, fileName, file.getAbsolutePath());
            partFileMap.put(name, fileBean);
            return this;
        }

        public OkHttpUtilFileBuilder addFormDataPart(String name, String value) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
                Log.e("OkHttpUtilFileBuilder", "addFormDataPart() params must not be null");
                return this;
            }
            RequestFileBean fileBean = new RequestFileBean(name, value, null);
            partFileMap.put(name, fileBean);
            return this;
        }

        public OkHttpUtilFileBuilder setHeaders(Map<String, String> headers) {
            this.headersMap = headers;
            return this;
        }

        public OkHttpUtilFileBuilder addHeader(String name, String value) {
            if (null == headersMap) {
                headersMap = new HashMap<>();
            }
            headersMap.put(name, value);
            return this;
        }

        /**
         * 获取参数的String形式
         * @param builder
         * @return
         */
        public static String getParamsString(OkHttpUtilFileBuilder builder) {
            if (null == builder) {
                return "";
            }
            JSONObject json = new JSONObject();
            for (String key : builder.paramsMap.keySet()) {
                try {
                    json.put(key, builder.paramsMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }

        public static OkHttpUtilFileBuilder requestUlr(String url) {
            OkHttpUtilFileBuilder okHttpUtilBuilder = new OkHttpUtilFileBuilder();
            okHttpUtilBuilder.url = url;
            return okHttpUtilBuilder;
        }

        /**
         * 取消请求
         */
        public void cancelRequest() {
            if (null != call) {
                PrintLineLog.i("cancelRequest");
                call.cancel();
            }
        }

        /**
         * 获取Json结果
         * @param callback
         * @throws JSONException
         */
        public <T> void postFileRequestCallObject(ResponseCallBack<T> callback) {
            call = OkHttpUtil.pullFileRequestData(url, headersMap, paramsMap, partFileMap, callback);
        }

        /**
         * 获取字符串结果
         * @param callback
         */
        public void postFileRequestCallString(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullFileRequestStringData(url, headersMap, paramsMap, partFileMap, callback);
        }

        /**
         * 获取字符串结果
         * 异步回调
         * @param callback
         */
        public void postFileRequestCallStringAsync(OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullFileRequestStringDataAsync(url, headersMap, paramsMap, partFileMap, callback);
        }
    }

    /**
     * 下载文件请求构造类
     */
    public static class OkHttpUtilDownloadFileBuilder {
        protected Map<String, String> paramsMap;
        protected Map<String, String> headersMap;
        protected String url;
        protected Call call;
        public OkHttpUtilDownloadFileBuilder() {
            paramsMap = new IdentityHashMap<>();
        }
        public OkHttpUtilDownloadFileBuilder addParams(String key, String params) {
            paramsMap.put(key, params);
            return this;
        }

        public OkHttpUtilDownloadFileBuilder setHeaders(Map<String, String> headers) {
            this.headersMap = headers;
            return this;
        }

        public OkHttpUtilDownloadFileBuilder addHeader(String name, String value) {
            if (null == headersMap) {
                headersMap = new HashMap<>();
            }
            headersMap.put(name, value);
            return this;
        }

        /**
         * 获取参数的String形式
         * @param builder
         * @return
         */
        public static String getParamsString(OkHttpUtilBuilder builder) {
            if (null == builder) {
                return "";
            }
            JSONObject json = new JSONObject();
            for (String key : builder.paramsMap.keySet()) {
                try {
                    json.put(key, builder.paramsMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }

        public static OkHttpUtilDownloadFileBuilder requestUlr(String url) {
            OkHttpUtilDownloadFileBuilder okHttpUtilBuilder =
                    new OkHttpUtilDownloadFileBuilder();
            okHttpUtilBuilder.url = url;
            return okHttpUtilBuilder;
        }

        /**
         * 取消请求
         */
        public void cancelRequest() {
            if (null != call) {
                PrintLineLog.i("cancelRequest");
                call.cancel();
            }
        }

        /**
         * 下载文件
         * @param callback
         * @throws JSONException
         */
        public <T> void postDownloadFile(File file, OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullDownloadFile(url, headersMap, paramsMap, file, callback);
        }

        /**
         * 下载文件
         * 异步回调
         * @param callback
         * @throws JSONException
         */
        public <T> void postDownloadFileAsync(File file, OkHttpUtilStringCallback callback) {
            call = OkHttpUtil.pullDownloadFileAsync(url, headersMap, paramsMap, file, callback);
        }
    }

    public interface OkHttpUtilStringCallback {
        void onFailed(int code, Exception e, long useTime, @Nullable Call call);
        void onResponse(int code, String response, long useTime, @Nullable Call call);
    }
}