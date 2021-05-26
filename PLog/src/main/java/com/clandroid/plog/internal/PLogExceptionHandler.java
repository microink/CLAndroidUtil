package com.clandroid.plog.internal;

import android.os.Process;
import androidx.annotation.AnyThread;

import com.clandroid.plog.core.PLog;
import com.clandroid.plog.core.PLogTag;

/**
 * Created by wyh on 2019/3/12.
 * 异常捕获输出
 */

public class PLogExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler mDefaultHandler;

    @AnyThread
    public PLogExceptionHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private static void logUncaughtException(Thread t, Throwable th) {
        StringBuilder message = new StringBuilder();
        message.append("FATAL EXCEPTION: ").append(t.getName()).append("\n");
        message.append("PID: ").append(Process.myPid()).append("\n");
        message.append("Thread: ").append(t.getName()).append("\n");
        message.append("Type: ").append(th.getClass().getName()).append("\n");
        message.append("Message: ").append(th.getMessage()).append("\n");
        for (StackTraceElement s : th.getStackTrace()) {
            message.append(" ")
                    .append(s.getClassName())
                    .append(" ")
                    .append(s.getMethodName())
                    .append("(")
                    .append(s.getFileName())
                    .append(":")
                    .append(s.getLineNumber())
                    .append(")\n");
        }
        PLog.e(PLogTag.EXCEPTION_TAG, message.toString());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logUncaughtException(t, e);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }

}
