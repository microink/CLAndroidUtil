package com.microink.clandroid.android.web;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

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
    public static boolean urlIsPDF(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        boolean isPDF = false;
        try {
            isPDF = new FetchContentTypeAsync(url).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isPDF;
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
