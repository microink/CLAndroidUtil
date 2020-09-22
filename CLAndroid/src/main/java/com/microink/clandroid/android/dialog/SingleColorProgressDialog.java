package com.microink.clandroid.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.microink.clandroid.R;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 12:00 PM
 *
 * A single color is added to the transparent ring load within the Dialog
 * 单独一个颜色到透明的圆环加载中Dialog
 */
public class SingleColorProgressDialog extends Dialog {

    private Context mContext;
    private Drawable mOldWindowDrawable;

    private int layoutId;

    public SingleColorProgressDialog(Context context, int style, int layout) {
        super(context, style);
        mContext = context;
        WindowManager.LayoutParams Params = getWindow().getAttributes();
        Params.width = WindowManager.LayoutParams.MATCH_PARENT;
        Params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(Params);
        layoutId = layout;
    }

    public SingleColorProgressDialog(Context context, int layout) {
        this(context, R.style.SingleColorProgressDialog, layout);
    }

    public SingleColorProgressDialog(Context context) {
        this(context, R.style.SingleColorProgressDialog, R.layout.cl_dialog_single_color_progress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        // Set the background color transparent to display rounded corners
        // 设置背景色透明以能够显示圆角
        Window window = getWindow();
        mOldWindowDrawable = window.getDecorView().getBackground();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void dismiss() {
        // Empty static references to avoid memory leaks
        // 置空静态引用，避免内存泄漏
        DialogUtil.clearLetNullProgressDialog();
        // Restore background color
        // 恢复背景色
        Window window = getWindow();
        window.setBackgroundDrawable(mOldWindowDrawable);

        super.dismiss();
    }
}
