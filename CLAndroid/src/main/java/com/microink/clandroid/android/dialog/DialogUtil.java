package com.microink.clandroid.android.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.microink.clandroid.R;

import androidx.appcompat.app.AlertDialog;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 2:28 PM
 *
 * Dialog util
 */
public class DialogUtil {
    private static SingleColorProgressDialog sProgressDialog;

    /**
     * Displays the loop progress Dialog
     * 显示圆环进度Dialog
     *
     * @param activity Activity
     */
    public static void showProgressDialog(Activity activity) {
        if (null != activity && !activity.isFinishing()) {
            sProgressDialog = new SingleColorProgressDialog(activity);
            sProgressDialog.setCancelable(false);
            sProgressDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        if (null == sProgressDialog) {
            return;
        }
        try {
            if (sProgressDialog.isShowing() && (null != activity && !activity.isFinishing())) {
                sProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }
        sProgressDialog = null;
    }

    /**
     * A callback when an Activity onDestory is destroyed,
     * used for dialogs that can be manually canceled by the user
     * Activity onDestory销毁时的回调，用于可被用户手动取消的Dialog
     */
    public static void activityDestory() {
        if (null == sProgressDialog) {
            return;
        }
        try {
            if (sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }
        sProgressDialog = null;
    }

    /**
     * Null static reference
     * 置空静态引用
     */
    public static void clearLetNullProgressDialog() {
        sProgressDialog = null;
    }

    /**
     * Displays a message, Dialog of two buttons
     * 显示一条消息，两个按钮的Dialog
     *
     * @param activity Activity
     * @param msg Msg
     * @param listener Click listener
     */
    public static void showOneMsgTwoBtnDialog(Activity activity, String msg,
            final DialogTwoBtnClickListener listener) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        final AlertDialog dlg = new AlertDialog.Builder(activity).show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cl_dialog_one_msg_two_btn);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView content = window.findViewById(R.id.content);
        content.setText(msg);
        Button cancelBtn = window.findViewById(R.id.btn_cancel_one_msg_two_btn_dialog);
        Button confirmBtn = window.findViewById(R.id.btn_confirm_one_msg_two_btn_dialog);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (null != listener) {
                    listener.clickCancel();
                }
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (null != listener) {
                    listener.clickConfirm();
                }
            }
        });
    }

    public interface DialogTwoBtnClickListener {
        /**
         * Click on the confirmation
         * 点击确认
         */
        void clickConfirm();

        /**
         * Click cancel
         * 点击取消
         */
        void clickCancel();
    }
}
