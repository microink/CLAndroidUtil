package com.microink.clandroid.android.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.TextView;

import com.microink.clandroid.android.color.ColorUtil;
import com.microink.clandroid.java.reflect.ReflectUtil;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/21 6:34 PM
 *
 * View util
 */
public class ViewUtil {

    /**
     * Get a Bitmap of the ImageView
     * 获取ImageView的Bitmap
     *
     * @param view view
     * @return bitmap of the ImageView
     */
    public static Bitmap getViewBitmap(View view) {
        if (null == view) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(),
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                view.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout((int) view.getX(), (int) view.getY(),
                (int) view.getX() + view.getMeasuredWidth(),
                (int) view.getY() + view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * View click effect adaptive, support to set THE XML drawable, single color
     * View点击效果自适配，支持设置xml的drawable、纯颜色
     *
     * @param view Click view 点击的视图
     */
    public static void viewPress(View view) {
        if (null == view) {
            return;
        }
        Drawable drawable = view.getBackground();

        if (drawable instanceof GradientDrawable) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                //ColorStateList colorStateList = gradientDrawable.getColor();
                //int color = colorStateList.getColorForState(gradientDrawable.getState(),
                //        Color.WHITE);
                //
                //
                ////ColorDrawable colorDrawable = (ColorDrawable) drawable;
                //int backgroundColor = color;
                //int newColor = getCurrentColor(0.4f, backgroundColor,
                //        Color.parseColor("#778899"));
                //view.setOnTouchListener(new View.OnTouchListener() {
                //    @Override
                //    public boolean onTouch(View v, MotionEvent event) {
                //        switch (event.getAction()) {
                //            case MotionEvent.ACTION_DOWN:
                //                gradientDrawable.setColor(newColor);
                //                view.setBackground(gradientDrawable);
                //                break;
                //            case MotionEvent.ACTION_UP:
                //            case MotionEvent.ACTION_CANCEL:
                //                gradientDrawable.setColor(backgroundColor);
                //                view.setBackground(gradientDrawable);
                //                break;
                //        }
                //        return false;
                //    }
                //});
            }

            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            Object mGradientState = ReflectUtil.getFieldValueByFieldName("mGradientState",
                    gradientDrawable);

            try {
                Class clazz = Class.forName("android.graphics.drawable.GradientDrawable$GradientState");

                ColorStateList colorStateList = (ColorStateList)
                        ReflectUtil.getFieldValueByFieldName("mSolidColors",
                                mGradientState);
                int[] state = gradientDrawable.getState();
                int color = colorStateList.getColorForState(state,
                        view.getResources().getColor(android.R.color.background_light));
                int newColor = ColorUtil.getCurrentColor(color,
                        Color.parseColor("#778899"), 0.4f);
                GradientDrawable newGradientDrawable = (GradientDrawable) gradientDrawable
                        .getConstantState().newDrawable().mutate();
                newGradientDrawable.setColor(newColor);

                // set different state drawable
                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[] {android.R.attr.state_pressed,
                        android.R.attr.state_enabled}, newGradientDrawable);
                stateListDrawable.addState(new int[] {-android.R.attr.state_pressed}, gradientDrawable);
                view.setBackground(stateListDrawable);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (drawable instanceof ColorDrawable) {
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
            view.setBackground(stateListDrawable);
        }
    }

    /**
     * TextView click on the effect, only change the text color,
     * for TextView without background
     * TextView点击效果，只改变文字颜色，用于无背景的TextView
     *
     * @param textView TextView
     */
    public static void textViewPressTextColorChange(TextView textView) {
        if (null == textView) {
            return;
        }
        int color = textView.getCurrentTextColor();
        int newColor = Color.parseColor("#2684FF");
        int[] pressed = new int[]{android.R.attr.state_pressed,
                android.R.attr.state_enabled};
        int[] normal = new int[] {-android.R.attr.state_pressed};
        ColorStateList colorStateList =
                new ColorStateList(new int[][]{pressed, normal}, new int[]{newColor, color});
        textView.setTextColor(colorStateList);
    }
}
