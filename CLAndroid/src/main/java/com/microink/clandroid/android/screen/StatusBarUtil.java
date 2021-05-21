package com.microink.clandroid.android.screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 7:10 PM
 *
 * Status bar tool class
 * 状态栏工具类
 */
public class StatusBarUtil {

    /**
     * Set immersion
     * 设置沉浸式
     *
     * @param activity Activity
     */
    public static void setImmersion(Activity activity) {

        if (null == activity) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Set the status bar color transparent
            // 设置状态栏颜色透明
            window.setStatusBarColor(Color.TRANSPARENT);

            int visibility = window.getDecorView().getSystemUiVisibility();
            // Layout content displayed in full screen
            // 布局内容全屏展示
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            // Hide the virtual navigation bar
            // 隐藏虚拟导航栏
            visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            // The first time the entire Activity does not respond to an event
            // after hiding the virtual navigation bar, use the following Flag
            // 隐藏虚拟导航栏后整个Activity第一次会不响应事件，用下面的Flag
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // Prevents content area sizes from changing
            // 防止内容区域大小发生变化
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            // Set the color of the virtual navigation bar
            // 设置虚拟导航栏颜色
            // window.setNavigationBarColor(Color.parseColor("#1bb5d7"));
            window.getDecorView().setSystemUiVisibility(visibility);
        }else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    /**
     * 设置沉浸式 但是不隐藏底部导航按钮
     *
     * @param activity Activity
     */
    public static void setImmersionNoHideNavigationButton(Activity activity) {

        if (null == activity) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Set the status bar color transparent
            // 设置状态栏颜色透明
            window.setStatusBarColor(Color.TRANSPARENT);

            int visibility = window.getDecorView().getSystemUiVisibility();
            // Layout content displayed in full screen
            // 布局内容全屏展示
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            // Hide the virtual navigation bar
            // 隐藏虚拟导航栏
            //visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            // The first time the entire Activity does not respond to an event
            // after hiding the virtual navigation bar, use the following Flag
            // 隐藏虚拟导航栏后整个Activity第一次会不响应事件，用下面的Flag
            //visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // Prevents content area sizes from changing
            // 防止内容区域大小发生变化
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            // Set the color of the virtual navigation bar
            // 设置虚拟导航栏颜色
            // window.setNavigationBarColor(Color.parseColor("#1bb5d7"));
            window.getDecorView().setSystemUiVisibility(visibility);
        }else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    /**
     * Gets the status bar height
     * 获取状态栏高度
     *
     * @param context Context
     * @return Status bar height
     */
    public static int getStatusBarHeight(Context context){
        int resId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resId > 0){
            return context.getResources().getDimensionPixelSize(resId);
        }
        return 0;
    }

    /**
     * Sets the offset of the layout child element behind the immersive status bar
     * 设置沉浸式状态栏后布局子元素的偏移量
     *
     * @param context Context
     * @param view Activity root view or view group
     */
    public static void setHeightAndPadding(Context context, View view){
        if (null == context || null == view) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.height += getStatusBarHeight(context);
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * Set the status bar black font icon
     * It is compatible with MIUI, Flyme, and other Android devices with
     * versions 4.4 and above
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUI、Flyme和6.0以上版本其他Android
     *
     * @param activity Activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int setStatusBarLightMode(Activity activity){
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(setMIUISetStatusBarLightMode(activity.getWindow(), true)){
                result = 1;
            } else if (setFlymeSetStatusBarLightMode(activity.getWindow(), true)){
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
                visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
                result = 3;
            }
        }
        return result;
    }

    /**
     * Set the status bar black font icon when the system type is known,
     * and apply to version 4.4 or above MIUI, Flyme and other Android versions 6.0 or above
     * 已知系统类型时设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUI、Flyme和6.0以上版本其他Android
     *
     * @param activity Activity
     * @param type System type 1:MIUUI 2:Flyme 3:android6.0
     */
    public static void setStatusBarLightMode(Activity activity, int type){
        if(1 == type) {
            setMIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (2 == type) {
            setFlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (3 == type){
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * Clear MIUI or Flyme or version 6.0 or above status bar black font
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
     *
     * @param activity Activity
     */
    public static int setStatusBarDarkMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (setMIUISetStatusBarLightMode(activity.getWindow(), false)) {
                result = 1;
            } else if (setFlymeSetStatusBarLightMode(activity.getWindow(), false)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
                visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
                activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
                result = 3;
            }
        }
        return result;
    }

    /**
     * Clear MIUI or Flyme or version 6.0 or above status bar black font by type
     * 根据和类型清除MIUI或flyme或6.0以上版本状态栏黑色字体
     *
     * @param activity Activity
     * @param type System type 1:MIUUI 2:Flyme 3:android6.0
     */
    public static void setStatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            setMIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            setFlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
            activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
        }
    }

    /**
     * Setting the status bar icon to a dark color and Meizu-specific
     * text style can be used to determine if you are a Flyme user
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window Need set window 需要设置的窗口
     * @param dark Dark True, light False
     * @return boolean Success return true
     */
    public static boolean setFlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (null != window) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * Set the status bar font icon to dark color. MIUI6 or above is required
     * 设置状态栏字体图标为深色，需要MIUI6以上
     *
     * @param window Need set window 需要设置的窗口
     * @param dark Dark True, light False 是否把状态栏字体及图标颜色设置为深色
     * @return boolean Success return true
     */
    public static boolean setMIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (null != window) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    // Status bar transparent and black font
                    // 状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    // Clear the black font
                    // 清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                //result = true;

                // The new MIUI status bar has changed color
                // 新版MIUI状态栏变色改了
                // https://dev.mi.com/console/doc/detail?pId=1159
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    result = true;
                }
            } catch (Exception e) {

            }
        }
        return result;
    }
}
