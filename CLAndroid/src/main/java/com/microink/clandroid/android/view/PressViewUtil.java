package com.microink.clandroid.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.microink.clandroid.android.color.ColorUtil;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/7/8 5:13 下午
 *
 * 视图按下效果Util
 */
public class PressViewUtil {

    /**
     * 给ImageView的设置了src单张图片，设置点击效果
     * @param context
     * @param imageView
     */
    public static void imageViewSrcImgPress(Context context, ImageView imageView) {
        if (null == context || null == imageView) {
            return;
        }
        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = drawable;
        Drawable pressed = changeBitmapBrightnessToDrawable(context, (bitmapDrawable).getBitmap());
        if (null == pressed) {
            return;
        }
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
        // View.ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_enabled }, normal);
        // View.EMPTY_STATE_SET
        bg.addState(new int[] {}, normal);
        imageView.setImageDrawable(bg);
    }

    /**
     * 给TextView的设置了背景单张图片，设置点击效果
     * @param context
     * @param textView
     */
    public static void textViewBGImgPress(Context context, TextView textView) {
        if (null == context || null == textView) {
            return;
        }
        Drawable drawable = textView.getBackground();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = drawable;
        Drawable pressed = changeBitmapBrightnessToDrawable(context, (bitmapDrawable).getBitmap());
        if (null == pressed) {
            return;
        }
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
        // View.ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_enabled }, normal);
        // View.EMPTY_STATE_SET
        bg.addState(new int[] {}, normal);
        textView.setBackground(bg);
    }

    /**
     * textView设置背景为单色的按下效果
     */
    public static void textViewBGColorPress(TextView textView) {
        if (null == textView) {
            return;
        }
        Drawable drawable = textView.getBackground();
        if (!(drawable instanceof ColorDrawable)) {
            return;
        }

        ColorDrawable colorDrawable = (ColorDrawable) drawable;
        int color = colorDrawable.getColor();
        int backgroundColor = color;
        int newColor = ColorUtil.getCurrentColor(backgroundColor,
                Color.parseColor("#778899"), 0.4f);
        ColorDrawable newColorDrawable = (ColorDrawable) colorDrawable
                .getConstantState().newDrawable().mutate();
        newColorDrawable.setColor(newColor);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_pressed,
                android.R.attr.state_enabled}, newColorDrawable);
        stateListDrawable.addState(new int[] {-android.R.attr.state_pressed}, colorDrawable);
        textView.setBackground(stateListDrawable);
    }

    /**
     * 创建一个bitmap的高亮的BitmapDrawable
     * @param context
     * @param srcBitmap
     * @return
     */
    private static Drawable changeBitmapBrightnessToDrawable(Context context, Bitmap srcBitmap){
        if (null == context || null == srcBitmap) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        int brightness = 60 - 127;
        ColorMatrix cMatrix = new ColorMatrix();
        // 改变亮度
        cMatrix.set(new float[] {
                1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个Bitmap
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        return new BitmapDrawable(context.getResources(), bmp);
    }
}
