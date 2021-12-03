package com.microink.clandroid.android.camera;

import android.graphics.Rect;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/12/3 7:05 下午
 *
 * 相机工具类
 */
public class CameraUtil {

    /**
     * 竖向Rect转为横向Rect
     * 用于相机读取Image为横向，而显示是竖向时，按竖向计算
     * 最后通过此函数转换为横向Image对应的矩形坐标
     * @param viewRect
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    public static Rect landscapeRectToPortraitRect(Rect viewRect, int viewWidth, int viewHeight) {
        if (null == viewRect) {
            return null;
        }
        if (0 >= viewRect.width() || 0 >= viewRect.height()) {
            return null;
        }
        Rect rect = new Rect();
        rect.left = viewRect.top;
        rect.top = viewWidth - viewRect.right;
        rect.right = viewRect.bottom;
        rect.bottom = rect.top + viewRect.width();
        return rect;
    }
}
