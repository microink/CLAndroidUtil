package com.clandroid.plog.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * Created by wyh on 2019/3/10.
 */

public class AppUtil {

    private static String sVersionName;
    private static String sCurProcessName;

    @NonNull
    public static String getVersionName(@NonNull Context context) {
        if (sVersionName != null) {
            return sVersionName;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return sVersionName = packageInfo.versionName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return sVersionName = "UNKNOWN";
        }
    }

    @NonNull
    public static String getCurProcessName(@NonNull Context context) {
        if (sCurProcessName != null) {
            return sCurProcessName;
        }
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            final int pid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return sCurProcessName = appProcess.processName;
                }
            }
        }
        return sCurProcessName = "UNKNOWN";
    }

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
