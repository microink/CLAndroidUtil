package com.microink.clandroid.android.log;

import android.os.Environment;
import android.util.Log;

import com.microink.clandroid.java.exception.ExceptionUtil;

import java.io.File;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 10:50 AM
 *
 * A Log utility class that prints line Numbers, method names, and class names
 * 可打印行号、方法名、类名的Log工具类
 */
public class PrintLineLog {
    /**
     * Log switch
     * Log开关
     */
    public static boolean LOG_DEBUG = false;
    /**
     * If the file exists locally, log is turned on
     * Log开启文件名
     */
    private static final String LOG_OPEN_FILE_NAME = "PrintLineLog";
    /**
     * Call stack depth
     * 调用栈深度
     */
    private static int deep = 4;

    /**
     * LOG v level
     */
    private static final int V_LEVEL = 1;
    /**
     * LOG d level
     */
    private static final int D_LEVEL = 2;
    /**
     * LOG i level
     */
    private static final int I_LEVEL = 3;
    /**
     * LOG w level
     */
    private static final int W_LEVEL = 4;
    /**
     * LOG e level
     */
    private static final int E_LEVEL = 5;

    /**
     * Is it the first entry
     */
    private static boolean isFirst = true;

    private PrintLineLog() {

    }

    /**
     * The v level prints the Log default tag file name
     * @param msg Log msg
     */
    public static void v(String msg) {
        switchLogLevel(null, msg, V_LEVEL);
    }

    public static void v(String msg, boolean saveFile) {
        switchLogLevel(null, msg, V_LEVEL);

        if (saveFile) {
            FileLog.getInstance().v(msg);
        }
    }

    /**
     * V level prints logs
     * @param tag Tag
     * @param msg Msg
     */
    public static void v(String tag, String msg) {
        switchLogLevel(tag, msg, V_LEVEL);
    }

    public static void v(String tag, String msg, boolean saveFile) {
        switchLogLevel(tag, msg, V_LEVEL);

        if (saveFile) {
            FileLog.getInstance().v(msg);
        }
    }

    public static void v(Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, V_LEVEL);
        }
    }

    public static void v(Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, V_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().v(msg);
        }
    }

    public static void v(String tag, Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, V_LEVEL);
        }
    }

    public static void v(String tag, Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, V_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().v(msg);
        }
    }

    /**
     * The d level prints the Log default tag file name
     * @param msg msg
     */
    public static void d(String msg) {
        switchLogLevel(null, msg, D_LEVEL);
    }

    public static void d(String msg, boolean saveFile) {
        switchLogLevel(null, msg, D_LEVEL);

        if (saveFile) {
            FileLog.getInstance().d(msg);
        }
    }

    /**
     * Level D prints the Log
     * @param tag Tag
     * @param msg Msg
     */
    public static void d(String tag, String msg) {
        switchLogLevel(tag, msg, D_LEVEL);
    }

    public static void d(String tag, String msg, boolean saveFile) {
        switchLogLevel(tag, msg, D_LEVEL);

        if (saveFile) {
            FileLog.getInstance().d(msg);
        }
    }

    public static void d(Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, D_LEVEL);
        }
    }

    public static void d(Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, D_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().d(msg);
        }
    }

    public static void d(String tag, Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, D_LEVEL);
        }
    }

    public static void d(String tag, Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, D_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().d(msg);
        }
    }

    /**
     * I level prints the Log default tag file name
     * @param msg Msg
     */
    public static void i(String msg) {
        switchLogLevel(null, msg, I_LEVEL);
    }

    public static void i(String msg, boolean saveFile) {
        switchLogLevel(null, msg, I_LEVEL);

        if (saveFile) {
            FileLog.getInstance().i(msg);
        }
    }

    /**
     * I level prints the Log
     * @param tag Tag
     * @param msg Msg
     */
    public static void i(String tag, String msg) {
        switchLogLevel(tag, msg, I_LEVEL);
    }

    public static void i(String tag, String msg, boolean saveFile) {
        switchLogLevel(tag, msg, I_LEVEL);

        if (saveFile) {
            FileLog.getInstance().i(msg);
        }
    }

    public static void i(Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, I_LEVEL);
        }
    }

    public static void i(Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, I_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().i(msg);
        }
    }

    public static void i(String tag, Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, I_LEVEL);
        }
    }

    public static void i(String tag, Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, I_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().i(msg);
        }
    }

    /**
     * The w level prints the Log default tag file name
     * @param msg Msg
     */
    public static void w(String msg) {
        switchLogLevel(null, msg, W_LEVEL);
    }

    public static void w(String msg, boolean saveFile) {
        switchLogLevel(null, msg, W_LEVEL);

        if (saveFile) {
            FileLog.getInstance().w(msg);
        }
    }

    /**
     * W level prints logs
     * @param tag Tag
     * @param msg Msg
     */
    public static void w(String tag, String msg) {
        switchLogLevel(tag, msg, W_LEVEL);
    }

    public static void w(String tag, String msg, boolean saveFile) {
        switchLogLevel(tag, msg, W_LEVEL);

        if (saveFile) {
            FileLog.getInstance().w(msg);
        }
    }

    public static void w(Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, W_LEVEL);
        }
    }

    public static void w(Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, W_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().w(msg);
        }
    }

    public static void w(String tag, Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, W_LEVEL);
        }
    }

    public static void w(String tag, Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, W_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().w(msg);
        }
    }

    /**
     * The e level prints the Log default tag file name
     * @param msg Msg
     */
    public static void e(String msg) {
        switchLogLevel(null, msg, E_LEVEL);
    }

    public static void e(String msg, boolean saveFile) {
        switchLogLevel(null, msg, E_LEVEL);

        if (saveFile) {
            FileLog.getInstance().e(msg);
        }
    }

    /**
     * Level E prints the Log
     * @param tag Tag
     * @param msg Msg
     */
    public static void e(String tag, String msg) {
        switchLogLevel(tag, msg, E_LEVEL);
    }

    public static void e(String tag, String msg, boolean saveFile) {
        switchLogLevel(tag, msg, E_LEVEL);

        if (saveFile) {
            FileLog.getInstance().e(msg);
        }
    }

    public static void e(Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, E_LEVEL);
        }
    }

    public static void e(Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(null, msg, E_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().e(msg);
        }
    }

    public static void e(String tag, Throwable throwable) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, E_LEVEL);
        }
    }

    public static void e(String tag, Throwable throwable, boolean saveFile) {
        String msg = ExceptionUtil.exceptionToString(throwable);

        if (LOG_DEBUG) {
            switchLogLevel(tag, msg, E_LEVEL);
        }

        if (saveFile) {
            FileLog.getInstance().e(msg);
        }
    }

    /**
     * Select different print levels according to type
     * @param tag Tag
     * @param msg Msg
     * @param level Level
     */
    private static void switchLogLevel(String tag, String msg, int level) {
        //if (isFirst) {
        //    // The first time you enter, determine if it is displayed
        //    try {
        //        // todo
        //        // Android 11 ExternalStorage update resolve
        //        File sdFile = Environment.getExternalStorageDirectory();
        //        File logOpenFile = new File(sdFile, LOG_OPEN_FILE_NAME);
        //        if (logOpenFile.exists()) {
        //            Log.i("PrintLineLog", "PrintLineLog file exists log open");
        //            LOG_DEBUG = true;
        //        }
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //    }
        //    isFirst = false;
        //
        //}
        if (!LOG_DEBUG) {
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        deep = (stackTraceElements.length - 1) < deep ? (stackTraceElements.length - 1) : deep;


        StackTraceElement mStackTraceElement = stackTraceElements[deep];
        String fileName = mStackTraceElement.getFileName();
        String strMsg = msg + " " + mStackTraceElement.getMethodName() + "()(" + fileName + ":" + mStackTraceElement.getLineNumber() + ")";
        //tag为null时将文件名设为tag
        if (null == tag) {
            String fileNameNoSuffix = fileName.split("\\.")[0];
            tag = fileNameNoSuffix;
        }
        switch (level) {
            case V_LEVEL :
                android.util.Log.v(tag, strMsg);
                break;
            case D_LEVEL :
                android.util.Log.d(tag, strMsg);
                break;
            case I_LEVEL :
                android.util.Log.i(tag, strMsg);
                break;
            case W_LEVEL :
                android.util.Log.w(tag, strMsg);
                break;
            case E_LEVEL :
                android.util.Log.e(tag, strMsg);
                break;
            default:
                android.util.Log.e(mStackTraceElement.getFileName().split("\\.")[0], "Log type is wrong");
        }
    }
}
