package com.microink.clandroid.android.file;

import android.content.Context;

import java.io.File;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/9/22 10:16 上午
 *
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获取应用专用的外部存储目录路径，如果外部存储不可用，则使用内部存储
     * @param context
     * @param dirName
     * @return
     */
    public static String getPriorityExternalFilesDirPath(Context context, String dirName) {
        if (null == context) {
            return "";
        }
        File file = context.getExternalFilesDir(dirName);
        if (null == file) {
            file = context.getFileStreamPath(dirName);
        }
        if (null == file) {
            return "";
        }
        return file.getAbsolutePath();
    }
}
