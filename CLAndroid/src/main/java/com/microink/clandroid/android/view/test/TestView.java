package com.microink.clandroid.android.view.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.microink.clandroid.R;
import com.microink.clandroid.android.data.SharedPreferenceUtil;
import com.microink.clandroid.android.screen.ActionBarUtil;
import com.microink.clandroid.android.screen.ScreenUtil;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/7/28 10:43 上午
 *
 * 显示测试环境的视图
 */
public class TestView extends ConstraintLayout implements View.OnClickListener {

    private static final int ANIMATOR_DURATION = 7000; // 动画默认持续时间

    private TextView mTVEnv; // 显示测试环境

    private ObjectAnimator objectAnimator; // 测试环境文本属性动画

    public TestView(Context context) {
        super(context);
        init(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.cl_view_test, this, true);

        mTVEnv = findViewById(R.id.tv_env_test);
        mTVEnv.setOnClickListener(this);
    }

    /**
     * 测试环境显示
     * @param activity
     */
    public static void testEnvShowView(Activity activity, boolean isTest, int statusBarHeight) {
         testEnvShowView(activity, isTest, statusBarHeight, ANIMATOR_DURATION);
    }

    /**
     * 测试环境显示
     * @param activity
     */
    public static void testEnvShowView(Activity activity, boolean isTest, int statusBarHeight,
            int animatorDuration) {
        if (!isTest) {
            return;
        }
        if (null == activity) {
            return;
        }
        Window window = activity.getWindow();
        if (null == window) {
            return;
        }
        View decorView = window.getDecorView();
        if (!(decorView instanceof ViewGroup)) {
            return;
        }
        TestView testView = new TestView(activity);
        int height = statusBarHeight + ActionBarUtil.getActionBarHeight(activity);
        ConstraintLayout.LayoutParams layoutParams =
                (LayoutParams) testView.mTVEnv.getLayoutParams();
        layoutParams.height = height;
        testView.mTVEnv.setLayoutParams(layoutParams);
        testView.mTVEnv.setPadding(0, statusBarHeight,
                ScreenUtil.dpToPx(activity, 60), 0);
        ViewGroup viewGroup = (ViewGroup) decorView;
        viewGroup.addView(testView);

        if (SharedPreferenceUtil.getBoolean(activity, SharedPreferenceUtil.CL_ANDROID,
                TestValue.PREFERENCE_KEY_HIDE, false)) {
            // 说明现在隐藏视图的
            testView.objectAnimator = ObjectAnimator.ofFloat(testView, "alpha",
                    0f, 1f, 0f, 1f, 0f, 1f, 0f);
            testView.objectAnimator.setDuration(animatorDuration);
            testView.objectAnimator.start();
        } else {
            // 说明现在是显示视图的
        }
    }

    /**
     * 点击测试环境文本
     */
    private void clickEnv() {
        if (SharedPreferenceUtil.getBoolean(getContext(), SharedPreferenceUtil.CL_ANDROID,
                TestValue.PREFERENCE_KEY_HIDE, false)) {
            // 说明现在隐藏视图的，点击应该显示
            SharedPreferenceUtil.saveBoolean(getContext(), SharedPreferenceUtil.CL_ANDROID,
                    TestValue.PREFERENCE_KEY_HIDE, false);
            if (null != objectAnimator && objectAnimator.isRunning()) {
                objectAnimator.cancel();
            }
            mTVEnv.setAlpha(1f);
            mTVEnv.setVisibility(VISIBLE);
        } else {
            // 说明现在是显示视图的，点击应该隐藏
            SharedPreferenceUtil.saveBoolean(getContext(), SharedPreferenceUtil.CL_ANDROID,
                    TestValue.PREFERENCE_KEY_HIDE, true);
            mTVEnv.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_env_test == id) {
            // 点击测试环境视图
            clickEnv();
        }
    }
}
