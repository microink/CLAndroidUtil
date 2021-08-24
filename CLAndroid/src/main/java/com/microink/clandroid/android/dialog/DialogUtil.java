package com.microink.clandroid.android.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.microink.clandroid.R;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 2:28 PM
 *
 * Dialog util
 */
public class DialogUtil {
    private static SingleColorProgressDialog sProgressDialog;
    private static SingleColorWithTextProgressDialog sProgressWithTextDialog;

    /**
     * Displays the loop progress Dialog
     * 显示圆环进度Dialog
     *
     * @param activity Activity
     */
    public static void showProgressDialog(Activity activity) {
        if (null != activity && !activity.isFinishing()) {
            if (null != sProgressDialog && sProgressDialog.isShowing()) {
                try {
                    sProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sProgressDialog = new SingleColorProgressDialog(
                    activity.getWindow().getDecorView().getSystemUiVisibility(),
                    activity);
            sProgressDialog.setCancelable(false);
            try {
                sProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示loading，带一条文字
     * @param activity
     * @param text
     */
    public static void showProgressWithTextDialog(Activity activity, String text) {
        if (null != activity && !activity.isFinishing()) {
            if (null != sProgressWithTextDialog && sProgressWithTextDialog.isShowing()) {
                try {
                    sProgressWithTextDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sProgressWithTextDialog = new SingleColorWithTextProgressDialog(
                    activity.getWindow().getDecorView().getSystemUiVisibility(),
                    activity);
            sProgressWithTextDialog.setCancelable(false);
            sProgressWithTextDialog.setText(text);
            try {
                sProgressWithTextDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            e.printStackTrace();
        }
        sProgressDialog = null;
    }

    public static void hideProgressWidthTextDialog(Activity activity) {
        if (null == sProgressWithTextDialog) {
            return;
        }
        try {
            if (sProgressWithTextDialog.isShowing() && (null != activity && !activity.isFinishing())) {
                sProgressWithTextDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sProgressWithTextDialog = null;
    }

    /**
     * A callback when an Activity onDestroy is destroyed,
     * used for dialogs that can be manually canceled by the user
     * Activity onDestroy销毁时的回调，用于可被用户手动取消的Dialog
     */
    public static void activityDestroy() {
        try {
            if (null != sProgressDialog && sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != sProgressWithTextDialog && sProgressWithTextDialog.isShowing()) {
                sProgressWithTextDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sProgressDialog = null;
        sProgressWithTextDialog = null;
    }

    /**
     * Null static reference
     * 置空静态引用
     */
    public static void clearLetNullProgressDialog() {
        sProgressDialog = null;
        sProgressWithTextDialog = null;
    }

    /**
     * 显示一条消息，两个按钮的Dialog
     * @param activity
     * @param msg
     * @param listener
     */
    public static void showOneMsgTwoBtnDialog(Activity activity, String msg,
            DialogTwoBtnClickListener listener) {
        showOneMsgTwoBtnDialog(activity, msg, null, null, listener);
    }

    /**
     * 显示一条消息，两个按钮的Dialog
     * @param activity
     * @param msg
     * @param listener
     */
    public static void showOneMsgTwoBtnDialog(Activity activity, String msg, String cancel,
            String confirm, final DialogTwoBtnClickListener listener) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        window.getDecorView().setSystemUiVisibility(activity.getWindow()
                .getDecorView().getSystemUiVisibility());
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(R.layout.cl_dialog_one_msg_two_btn);
        TextView content = window.findViewById(R.id.tv_msg_one_msg_two_btn_dialog);
        content.setText(msg);
        Button cancelBtn = window.findViewById(R.id.btn_cancel_one_msg_two_btn_dialog);
        Button confirmBtn = window.findViewById(R.id.btn_confirm_one_msg_two_btn_dialog);
        if (!TextUtils.isEmpty(confirm)) {
            confirmBtn.setText(confirm);
        }
        if (!TextUtils.isEmpty(cancel)) {
            cancelBtn.setText(cancel);
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != listener) {
                    listener.clickCancel();
                }
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != listener) {
                    listener.clickConfirm();
                }
            }
        });

        int uiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        dlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        try {
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (0 != uiVisibility) {
            dlg.getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        }
        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * 显示一条消息，一个按钮的Dialog
     * @param activity
     * @param msg
     * @param listener
     */
    public static void showOneMsgOneBtnDialog(Activity activity, String msg,
            DialogOneBtnClickListener listener) {
        showOneMsgOneBtnDialog(activity, msg, null, listener);
    }

    /**
     * 显示一条消息，一个按钮的Dialog
     * @param activity
     * @param msg
     * @param listener
     */
    public static void showOneMsgOneBtnDialog(Activity activity, String msg, String confirm,
            final DialogOneBtnClickListener listener) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        window.getDecorView().setSystemUiVisibility(activity.getWindow()
                .getDecorView().getSystemUiVisibility());
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(R.layout.cl_dialog_one_msg_one_btn);
        TextView content = window.findViewById(R.id.tv_msg_one_msg_two_btn_dialog);
        content.setText(msg);
        Button confirmBtn = window.findViewById(R.id.btn_confirm_one_msg_two_btn_dialog);
        if (!TextUtils.isEmpty(confirm)) {
            confirmBtn.setText(confirm);
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != listener) {
                    listener.clickConfirm();
                }
            }
        });

        int uiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        dlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        try {
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (0 != uiVisibility) {
            dlg.getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        }
        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public interface DialogTwoBtnClickListener {
        /**
         * 点击确认
         */
        void clickConfirm();

        /**
         * 点击取消
         */
        void clickCancel();
    }

    public interface DialogOneBtnClickListener {
        /**
         * 点击确认
         */
        void clickConfirm();
    }
}
