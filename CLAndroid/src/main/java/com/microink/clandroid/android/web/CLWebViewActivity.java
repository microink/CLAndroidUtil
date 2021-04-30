package com.microink.clandroid.android.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microink.clandroid.R;
import com.microink.clandroid.android.screen.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/20/21 5:24 PM
 *
 * 独立进程的WebView的Activity
 */
public class CLWebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String INTENT_KEY_URL = "URL"; // intent传值key：url
    private static final String INTENT_KEY_FINISH = "backFinish"; // intent传值key：是否返回就关闭页面
    private static final int REQUEST_CODE_SELECT_FILE = 9001; // 选择文件的RequestCode

    private LinearLayout mLLAll; // 整体布局
    private ImageButton mIBTBack; // 返回
    private TextView mTVTitle; // 标题
    private WebView mWebView; // WebView

    private String url; // 加载的url
    private boolean isBackFinish; // 返回就关闭页面的标识

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式
        StatusBarUtil.setImmersion(this);
        setContentView(R.layout.cl_activity_web_view);

        initData();
        initView();

        initWebView();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        url = intent.getStringExtra(INTENT_KEY_URL);
        isBackFinish = intent.getBooleanExtra(INTENT_KEY_FINISH, false);
    }

    private void initView() {
        mLLAll = findViewById(R.id.cl_ll_all_web_view);
        mIBTBack = findViewById(R.id.cl_ibt_back_web_view);
        mTVTitle = findViewById(R.id.cl_tv_title_web_view);
        mWebView = findViewById(R.id.cl_web_view);

        mIBTBack.setOnClickListener(this);

        // 设置状态栏padding，避免内容覆盖到状态栏上
        StatusBarUtil.setHeightAndPadding(this, mLLAll);
        // 设置白色状态栏 黑色文字
        StatusBarUtil.setStatusBarLightMode(this);
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        // 先清空缓存
        mWebView.clearCache(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        // 设置支持脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置支持DOM Storage
        mWebView.getSettings().setDomStorageEnabled(true);
        // 解决图片不显示
        mWebView.getSettings().setBlockNetworkImage(false);
        // 解决引用http地址的图片不显示
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    public void onBackPressed() {
        if (isBackFinish) {
            super.onBackPressed();
        } else {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * 打开默认的系统图片选择应用的Intent
     * @return
     */
    private Intent createDefaultImageOpenableIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    /**
     * 跳转到WebActivity
     * @param context
     */
    public static void jumpWebActivity(Context context, String url, boolean isBackFinish) {
        Intent intent = new Intent(context, CLWebViewActivity.class);
        intent.putExtra(INTENT_KEY_URL, url);
        intent.putExtra(INTENT_KEY_FINISH, isBackFinish);
        context.startActivity(intent);
    }

    /**
     * 点击返回
     */
    private void clickBack() {
        if (isBackFinish) {
            finish();
        } else {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.cl_ibt_back_web_view == id) {
            clickBack();
        }
    }

    private final class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(!TextUtils.isEmpty(view.getTitle())){
                String title = view.getTitle();
                mTVTitle.setText(title);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            /**
             * 自定义js对话框
             */
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            startActivityForResult(createDefaultImageOpenableIntent(),
                    REQUEST_CODE_SELECT_FILE);
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
                String capture) {
            openFileChooser(uploadMsg, acceptType);
        }

        // For Android 5.0+
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
            //            if (mUploadMessageArray != null) return true;
            startActivityForResult(createDefaultImageOpenableIntent(), REQUEST_CODE_SELECT_FILE);
            return true;
        }
    }
}
