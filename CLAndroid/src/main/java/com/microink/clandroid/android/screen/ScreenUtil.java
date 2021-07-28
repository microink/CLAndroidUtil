package com.microink.clandroid.android.screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 6:42 PM
 *
 * Screen tools class
 * 屏幕工具类
 */
public class ScreenUtil {

    /**
     * Gets the Activity width, excluding the status bar width
     * 获取Activity宽度，不包含状态栏高度
     *
     * @param context Context
     * @return Activity width, excluding the status bar width
     */
    public static int getActivityWidth(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * Gets the Activity height, excluding the status bar height
     * 获取Activity高度，不包含状态栏高度
     *
     * @param context Context
     * @return Activity height, excluding the status bar height
     */
    public static int getActivityHeight(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * Gets the Activity width, including the status bar width
     * 获取Activity宽度，不包含状态栏高度
     *
     * @param activity Activity
     * @return Activity width, including the status bar width
     */
    public static int getScreenWidth(Activity activity) {
        if (null == activity) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Gets the Activity height, including the status bar height
     * 获取Screen高度，含状态栏高度
     *
     * @param activity Activity
     * @return Activity height, including the status bar height
     */
    public static int getScreenHeight(Activity activity) {
        if (null == activity) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Dp to Px
     * dp转像素px
     *
     * @param context Context
     * @param dpValue Dp value
     * @return Px value
     */
    public static int dpToPx(Context context, float dpValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics()));
    }

    /**
     * Px to Dp
     * 像素px转dp
     *
     * @param context Context
     * @param pxValue Px value
     * @return Dp value
     */
    public static int pxToDp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕英寸数
     * @param context
     * @return
     */
    public static float screenSizeInch(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);

        return (float) screenInches;
    }

    /**
     * Google官方的判断是否是平板的方法，不能兼容全部
     * @param context
     * @return
     */
    public static boolean isPadFromGoogle(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
