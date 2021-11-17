package com.microink.clandroid.android.executor;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 6:12 PM
 *
 * Common thread pool management class
 * 通用线程池管理类
 */
public class AppExecutors {

    private final Executor mDiskIO;

    private final Executor mNetworkIO;

    private final MainThreadExecutor mMainThread;

    private final Executor mNormalThread;

    private AppExecutors(Executor diskIO, Executor networkIO, MainThreadExecutor mainThread, Executor normalThread) {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
        this.mNormalThread = normalThread;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(), Executors.newCachedThreadPool());
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public MainThreadExecutor mainThread() {
        return mMainThread;
    }

    public Executor normalThread() {
        return mNormalThread;
    }


    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

        public void executeDelay(Runnable runnable, long time) {
            mainThreadHandler.postDelayed(runnable, time);
        }

        /**
         * 移除所有消息
         */
        public void removeAll() {
            mainThreadHandler.removeCallbacksAndMessages(null);
        }
    }
}
