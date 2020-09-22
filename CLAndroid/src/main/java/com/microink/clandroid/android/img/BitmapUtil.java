package com.microink.clandroid.android.img;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 6:06 PM
 *
 * Bitmap common util
 */
public class BitmapUtil {

    /**
     * The two bitmaps are overlaid into a bitmap based on the length
     * and width of the underlying bitmap.
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     *
     * @param backBitmap Bitmap at the bottom 在底部的位图
     * @param frontBitmap The bitmap covered above 盖在上面的位图
     * @return result bitmap 结果Bitmap
     */
    public static Bitmap mergeOnBackRectBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {

            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }
}
