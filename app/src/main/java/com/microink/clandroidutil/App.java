package com.microink.clandroidutil;

import android.app.Application;

import com.microink.clandroid.android.executor.AppExecutors;

import java.util.concurrent.Executor;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 6:22 PM
 *
 * Custom application
 * 自定义Application
 */
public class App extends Application {

    private static App sInstance;

    private AppExecutors appExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initExecutors();
    }

    public static App getInstance() {
        return sInstance;
    }

    /**
     * Initializes the thread pool
     * 初始化线程池
     */
    private void initExecutors() {
        appExecutors = new AppExecutors();
    }

    /**
     * Gets the standard thread pool
     * 获取标准线程池
     * @return standard thread pool
     */
    public Executor getNormalExecutor() {
        return appExecutors.normalThread();
    }

    /**
     * Gets the main thread pool
     * 获取主线程线程池
     * @return main thread pool
     */
    public Executor getMainExecutor() {
        return appExecutors.mainThread();
    }

    /**
     * Gets the local storage thread pool
     * 获取本地存储线程池
     * @return local storage thread pool
     */
    public Executor getIOExecutor() {
        return appExecutors.diskIO();
    }
}
