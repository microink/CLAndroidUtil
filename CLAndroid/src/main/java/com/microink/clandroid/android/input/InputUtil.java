package com.microink.clandroid.android.input;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 11:17 AM
 *
 * Input util
 */
public class InputUtil {

    private static volatile InputUtil sInstance;

    private InputMethodManager inputMethodManager;

    private InputUtil(Context application) {
        inputMethodManager = (InputMethodManager) application.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static InputUtil getInstance(Context application) {
        if (null == sInstance) {
            synchronized (InputUtil.class) {
                if (null == sInstance) {
                    sInstance = new InputUtil(application);
                }
            }
        }
        return sInstance;
    }

    /**
     * Show input
     * 显示键盘
     */
    public void showInput(View view) {
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * Hide input
     * 隐藏键盘
     */
    public void hideInput(View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Hide bottom layout when input method is up,
     * It must be called before the setContentView
     * 当输入法弹起时隐藏底部布局，必须在setContentView之前调用
     *
     * @param activity Activity
     */
    public static void showInputHideBottomView(Activity activity) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        activity.getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //setContentView(R.layout.activity_main);
    }
}
