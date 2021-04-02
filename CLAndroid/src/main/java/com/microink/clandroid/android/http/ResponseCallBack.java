package com.microink.clandroid.android.http;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/17 5:34 PM
 *
 * 通用Json解析的OkHttp返回结果回调类
 * !!!必须写超类，否则无法获取到泛型类型
 */
public abstract class ResponseCallBack<T> extends AbsCallback<T> {

    public void parseNetworkResponseStr(final int code, final String data, final Call call) throws Exception{
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            //如果写了泛型，就会进入这里，否者不会执行
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type beanType = parameterizedType.getActualTypeArguments()[0];
            Gson gson = new Gson();
            final T result = gson.fromJson(data, beanType);
            OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                @Override
                public void run() {
                    onResponse(code, result, data, call);
                }
            });
        } else {
            // 没写泛型直接返回
            OkHttpUtil.getInstance().executeFunInMain(new Runnable() {
                @Override
                public void run() {
                    onFailed(code, new Exception("must write type class"), data, call);
                }
            });
        }

        //Gson gson = new Gson();
        //T result = gson.fromJson(data, clazz);
        //callback.onResponse(code, result, data, call);
    }
}
