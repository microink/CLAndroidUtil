package com.microink.clandroid.android.web;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/8/21 5:49 PM
 *
 * Web工具类
 */
public class WebUtil {

    /**
     * 检查该URL是否是PDF，通过url返回的结果判断
     * @param url
     * @return
     */
    public static void urlIsPDF(final String url, Executor executor, final PDFCheckCallback callback) {
        if (TextUtils.isEmpty(url)) {
            if (null != callback) {
                callback.isPDF(false);
            }
            return;
        }
        if (null == executor) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isDownloadableFile = false;
                    try {
                        URL urlCheck = new URL(url);
                        URLConnection urlConnection = urlCheck.openConnection();
                        String contentType = urlConnection.getContentType();
                        isDownloadableFile = contentType.equalsIgnoreCase("application/pdf");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != callback) {
                        callback.isPDF(isDownloadableFile);
                    }
                }
            });
            thread.start();
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    boolean isDownloadableFile = false;
                    try {
                        URL urlCheck = new URL(url);
                        URLConnection urlConnection = urlCheck.openConnection();
                        String contentType = urlConnection.getContentType();
                        isDownloadableFile = contentType.equalsIgnoreCase("application/pdf");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != callback) {
                        callback.isPDF(isDownloadableFile);
                    }
                }
            });
        }
    }

    /**
     * 检查是否是PDF回调
     */
    public interface PDFCheckCallback {
        void isPDF(boolean isPF);
    }

    public static class FetchContentTypeAsync extends AsyncTask<Void, Void, Boolean> {

        private String requestedUrl;

        public FetchContentTypeAsync(String requestedUrl) {
            this.requestedUrl = requestedUrl;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isDownloadableFile = false;
            try {
                URL url = new URL(requestedUrl);
                URLConnection urlConnection = url.openConnection();
                String contentType = urlConnection.getContentType();
                isDownloadableFile = contentType.equalsIgnoreCase("application/pdf");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isDownloadableFile;
        }
    }
}
