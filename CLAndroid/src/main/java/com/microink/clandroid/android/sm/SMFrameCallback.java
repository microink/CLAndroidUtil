package com.microink.clandroid.android.sm;

import android.view.Choreographer;

import com.microink.clandroid.android.log.PrintLineLog;

import java.util.concurrent.TimeUnit;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/7/21 11:07 AM
 *
 * 流畅度监听类
 */
public class SMFrameCallback implements Choreographer.FrameCallback {
    private static final String TAG = "SMFrameCallback";
    public static final float deviceRefreshRateMs = 16.6f;
    public static long lastFrameTimeNanos = 0; // 纳秒为单位
    public static long currentFrameTimeNanos = 0;
    public static volatile SMFrameCallback sInstance;

    public void start() {
        Choreographer.getInstance().postFrameCallback(SMFrameCallback.getInstance());
    }

    public static SMFrameCallback getInstance() {
        if (null == sInstance) {
            synchronized (SMFrameCallback.class) {
                if (null == sInstance) {
                    sInstance = new SMFrameCallback();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (lastFrameTimeNanos == 0) {
            lastFrameTimeNanos = frameTimeNanos;
            Choreographer.getInstance().postFrameCallback(this);
            return;
        }
        currentFrameTimeNanos = frameTimeNanos;
        // 计算两次doFrame的时间间隔
        float value = (currentFrameTimeNanos - lastFrameTimeNanos) / 1000000f;

        int skipFrameCount = skipFrameCount(lastFrameTimeNanos, currentFrameTimeNanos, deviceRefreshRateMs);

        //PrintLineLog.e(TAG, "两次绘制时间间隔value=" + value + "  frameTimeNanos=" +
        //        frameTimeNanos + "  currentFrameTimeNanos=" + currentFrameTimeNanos +
        //        "  skipFrameCount=" + skipFrameCount + "");
        float smFloat = 1000 / value;
        String showStr = String.format("%.1f", smFloat);
        if (smFloat <= 20f) {
            // 卡死
            PrintLineLog.e(TAG, "流畅度 " + showStr);
        } else if (smFloat <= 40f) {
            // 很卡
            PrintLineLog.w(TAG, "流畅度 " + showStr);
        } else if (smFloat <= 50f) {
            // 较为卡
            PrintLineLog.i(TAG, "流畅度 " + showStr);
        } else if (smFloat < 59f) {
            // 不卡
            PrintLineLog.d(TAG, "流畅度 " + showStr);
        }


        lastFrameTimeNanos = currentFrameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }

    /**
     * 计算跳过多少帧
     */
    private int skipFrameCount(long start, long end, float devRefreshRate) {
        int count = 0;
        long diffNs = end - start;

        long diffMs = TimeUnit.MILLISECONDS.convert(diffNs, TimeUnit.MILLISECONDS);
        long dev = Math.round(devRefreshRate);
        if (diffMs > dev) {
            long skipCount = diffMs / dev;
            count = (int) skipCount;
        }
        return count;
    }
}