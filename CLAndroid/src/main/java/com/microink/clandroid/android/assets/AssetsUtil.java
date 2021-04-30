package com.microink.clandroid.android.assets;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/26/21 2:31 PM
 *
 * Assets工具类
 */
public class AssetsUtil {

    /**
     * 将assets文件夹下的文件拷贝至本地路径
     * @param appCtx
     * @param srcPath
     * @param dstPath
     */
    public static void copyFileFromAssets(Context appCtx, String srcPath, String dstPath) {
        if (srcPath.isEmpty() || dstPath.isEmpty()) {
            return;
        }
        String dstDir = dstPath.substring(0, dstPath.lastIndexOf('/'));
        if (dstDir.length() > 0) {
            recursiveCreateDirectories(dstDir);
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(appCtx.getAssets().open(srcPath));
            os = new BufferedOutputStream(new FileOutputStream(new File(dstPath)));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将asstes文件夹下的文件夹整个拷贝至本地路径
     * @param appCtx
     * @param srcDir
     * @param dstDir
     */
    public static void copyDirectoryFromAssets(Context appCtx, String srcDir, String dstDir) {
        if (srcDir.isEmpty() || dstDir.isEmpty()) {
            return;
        }
        try {
            if (!new File(dstDir).exists()) {
                new File(dstDir).mkdirs();
            }
            for (String fileName : appCtx.getAssets().list(srcDir)) {
                String srcSubPath = srcDir + File.separator + fileName;
                String dstSubPath = dstDir + File.separator + fileName;
                if (new File(srcSubPath).isDirectory()) {
                    copyDirectoryFromAssets(appCtx, srcSubPath, dstSubPath);
                } else {
                    copyFileFromAssets(appCtx, srcSubPath, dstSubPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环创建文件夹
     * @param fileDir
     */
    public static void recursiveCreateDirectories(String fileDir) {
        String[] fileDirs = fileDir.split("\\/");
        String topPath = "";
        for (int i = 0; i < fileDirs.length; i++) {
            topPath += "/" + fileDirs[i];
            File file = new File(topPath);
            if (file.exists()) {
                continue;
            } else {
                file.mkdir();
            }
        }
    }
}
