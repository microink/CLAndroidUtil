package com.microink.clandroid.android.application;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 10:25 AM
 *
 * Application Util
 */
public class ApplicationUtil {

    /**
     * Determine if it is the main thread
     * 判断是否是主线程
     *
     * @return True in main process,false not in main process
     */
    public static boolean inMainProcess(Context context) {
        if (null == context) {
            throw new NullPointerException("Context must be not null");
        }
        String packageName = context.getPackageName();
        String processName = getProcessName(android.os.Process.myPid());
        return packageName.equals(processName);
    }

    /**
     * Gets the process name corresponding to the process number
     * 获取进程号对应的进程名
     *
     * @param pid Process id
     * @return Process name
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
