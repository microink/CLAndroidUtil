package com.microink.clandroid;

import android.app.Application;
import android.content.Context;

import com.microink.clandroid.android.carsh.CLCrashHandler;
import com.microink.clandroid.android.log.FileLog;
import com.microink.clandroid.android.log.PrintLineLog;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/19/21 3:50 PM
 *
 * CLAndroidUtil工具类总类
 */
public final class CLAndroidUtil {

    public static final String VERSION = BuildConfig.VERSION_NAME;

    public static Application sContext;

    /**
     * 初始化
     * @param context
     */
    public static void init(Application context) {
        sContext = context;

        CLCrashHandler clCrashHandler = CLCrashHandler.getInstance();
        clCrashHandler.init(context);

        FileLog fileLog = FileLog.getInstance();
    }

    public static void init(Application context, boolean logOpen, boolean fileLogOpen) {
        sContext = context;

        CLCrashHandler clCrashHandler = CLCrashHandler.getInstance();
        clCrashHandler.init(context);

        FileLog fileLog = FileLog.getInstance();

        PrintLineLog.LOG_DEBUG = logOpen;
        FileLog.LOG_DEBUG_FILE = fileLogOpen;
    }
}
