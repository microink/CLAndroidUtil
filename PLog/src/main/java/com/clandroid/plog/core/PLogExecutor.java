package com.clandroid.plog.core;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wyh on 2019/3/13.
 * 执行池
 */
public enum PLogExecutor {
    INSTANCE;

    private static final String TAG = "PLogExecutor";

    private final ExecutorService mDiskExecutor = Executors.newSingleThreadExecutor(new ExecutorsThreadFactory("disk-io"));
    private final ExecutorService mNetworkExecutor = Executors.newSingleThreadExecutor(new ExecutorsThreadFactory("net-upload"));
    private final MainThreadExecutor mMainExecutor = new MainThreadExecutor();


    public static PLogExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * 文件读写线程池
     */
    public static ExecutorService getDiskExecutor() {
        return getInstance().mDiskExecutor;
    }

    /**
     * 网络操作线程池
     */
    public static ExecutorService getNetworkExecutor() {
        return getInstance().mNetworkExecutor;
    }

    /**
     * 主线程
     */
    public static MainThreadExecutor getMainExecutor() {
        return getInstance().mMainExecutor;
    }

    /**
     * 文件读写操作
     */
    public static void executeDisk(Runnable runnable) {
        getDiskExecutor().execute(runnable);
    }

    /**
     * 网络操作
     */
    public static void executeNetwork(Runnable runnable) {
        getNetworkExecutor().execute(runnable);
    }

    /**
     * 主线程操作
     */
    public static void executeMain(Runnable runnable) {
        getMainExecutor().execute(runnable);
    }


    //主线程
    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

        public Handler getMainThreadHandler() {
            return mainThreadHandler;
        }
    }

    private static class ExecutorsThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(1);
        private String name;

        ExecutorsThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, TAG + name + "-Thread-" + count.getAndIncrement());
        }
    }

}
