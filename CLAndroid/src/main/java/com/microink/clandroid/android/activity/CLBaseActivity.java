package com.microink.clandroid.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.microink.clandroid.android.screen.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/6/21 10:03 AM
 *
 * BaseActivity
 */
public abstract class CLBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected CLBaseActivity mBaseActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        setImmersion();
        beforeSetContentView();
        int layoutId = getLayoutId();
        if (0 != layoutId) {
            setContentView(layoutId);
        }
        initData();
        initView();
        setImmersionHeightAndPadding();
        fullUI();
    }

    /**
     * 设置沉浸式，如果不需要则复写该方法为空
     */
    protected void setImmersion() {
        setImmersionAll();
    }

    /**
     * 设置全部的沉浸式
     */
    protected void setImmersionAll() {
        StatusBarUtil.setImmersionNoHideNavigationButton(this);
    }

    /**
     * 设置沉浸式，但是不隐藏底部导航
     */
    protected void setImmersionNoHideNavigationButton() {
        StatusBarUtil.setImmersionNoHideNavigationButton(this);
    }

    /**
     * 在setContentView()方法调用前要进行的操作
     */
    protected void beforeSetContentView() {

    }

    /**
     * 设置布局ID
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据，在视图绘制前的一些处理
     */
    protected void initData() {

    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 设置黑色状态栏文字
     * @param lightMode true 黑色文字 false 白色文字
     */
    protected void setStatusBarLightMode(boolean lightMode) {
        int result = lightMode ? StatusBarUtil.setStatusBarLightMode(this)
                : StatusBarUtil.setStatusBarDarkMode(this);
    }

    /**
     * 获取Activity根布局ViewGroup，为沉浸式设置内上边距
     * @return
     */
    protected abstract View getImmersionRootPaddingView();

    /**
     * 设置沉浸式的根布局顶部padding，如果不使用沉浸式则复写此方法为空
     */
    protected void setImmersionHeightAndPadding() {
        StatusBarUtil.setHeightAndPadding(this, getImmersionRootPaddingView());
    }

    /**
     * 填充UI布局
     */
    protected abstract void fullUI();

    /**
     * 显示Toast
     * @param str
     */
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}