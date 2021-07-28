package com.microink.clandroid.android.screen;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/7/28 11:47 上午
 *
 * ActionBar工具类
 */
public class ActionBarUtil {

    /**
     * 获取ActionBar高度
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                true);
        int actionBarHeight = context.getResources().getDimensionPixelSize(tv.resourceId);
        return actionBarHeight;
    }
}
