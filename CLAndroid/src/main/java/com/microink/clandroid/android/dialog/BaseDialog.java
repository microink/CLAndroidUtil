package com.microink.clandroid.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.microink.clandroid.R;
import com.microink.clandroid.android.screen.ScreenUtil;

import androidx.annotation.NonNull;

/**
 * @author Cass
 * @version v1.0
 * @Date 2019/11/14 15:20
 *
 * Dialog基类
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener {

    private boolean canceledOnTouchOutside; // 是否可以外部点击消失
    private int width; // 宽
    private int height; // 高
    private int gravity; // 位置

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.CLBaseDialogStyle);

        int screenWidth = ScreenUtil.getActivityWidth(context);
        int screenHeight = ScreenUtil.getActivityHeight(context);
        initDialog(context, screenWidth, screenHeight, Gravity.CENTER, true);
    }

    public BaseDialog(@NonNull Context context, boolean canceledOnTouchOutside) {
        super(context, R.style.CLBaseDialogStyle);

        int screenWidth = ScreenUtil.getActivityWidth(context);
        int screenHeight = ScreenUtil.getActivityHeight(context);
        initDialog(context, screenWidth, screenHeight, Gravity.CENTER, canceledOnTouchOutside);
    }

    public BaseDialog(@NonNull Context context, float widthDP, int heightDP) {
        super(context, R.style.CLBaseDialogStyle);

        initDialog(context, ScreenUtil.dpToPx(context, widthDP),
                ScreenUtil.dpToPx(context, heightDP),
                Gravity.CENTER, true);
    }

    public BaseDialog(@NonNull Context context, float widthDP, float heightDP,
            boolean canceledOnTouchOutside) {
        super(context, R.style.CLBaseDialogStyle);

        initDialog(context, ScreenUtil.dpToPx(context, widthDP),
                ScreenUtil.dpToPx(context, heightDP),
                Gravity.CENTER, canceledOnTouchOutside);
    }

    public BaseDialog(@NonNull Context context, float widthDP, float heightDP,
            int gravity, boolean canceledOnTouchOutside) {
        super(context, R.style.CLBaseDialogStyle);

        initDialog(context, ScreenUtil.dpToPx(context, widthDP),
                ScreenUtil.dpToPx(context, heightDP),
                gravity, canceledOnTouchOutside);
    }

    private void initDialog(Context context, int width, int height, int gravity, boolean canceledOnTouchOutside) {
        this.width = width;
        this.height = height;
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        this.gravity = gravity;

        int screenWidth = ScreenUtil.getActivityWidth(context);
        int screenHeight = ScreenUtil.getActivityHeight(context);
        // 匹配屏幕
        if (0 == this.width) {
            this.width = screenWidth;
        }
        if (0 == this.height) {
            this.height = screenHeight;
        }

        // 在构造函数中调用，避免没有show的时候view为空，无法设置view的相关属性
        setContentView(getLayoutId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(canceledOnTouchOutside);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            params.height = height;
            params.gravity = gravity;
            switch (gravity) {
                case Gravity.CENTER:
                    window.setWindowAnimations(R.style.CLBaseDialogAnimCenter);
                    break;
                case Gravity.BOTTOM:
                    window.setWindowAnimations(R.style.CLBaseDialogAnimBottom);
                    break;
            }
            window.setAttributes(params);
        }

        initView();
        fullUI();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void fullUI();

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
