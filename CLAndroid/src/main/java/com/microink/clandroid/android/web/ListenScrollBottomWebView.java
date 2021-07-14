package com.microink.clandroid.android.web;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/25/21 2:28 PM
 *
 * 监听滑动到底部的WebView
 */
public class ListenScrollBottomWebView extends WebView {

    private WebViewScrollBottomListener scrollBottomListener; // 滑动到底部监听器

    public ListenScrollBottomWebView(Context context) {
        super(context);
    }

    public ListenScrollBottomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenScrollBottomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListenScrollBottomWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ListenScrollBottomWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    /**
     * 设置滑动到底部监听
     * @param listener
     */
    public void setScrollBottomListener(WebViewScrollBottomListener listener) {
        scrollBottomListener = listener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        // 如果需要只有在底部才回调，往上滑动就不再回调使用下面判断
        // clampedY && 0 != scrollY
        if (clampedY && 0 != scrollY) {
            // 到底了
            if (null != scrollBottomListener) {
                scrollBottomListener.onScrollBottom();
            }
        }
    }

    /**
     * 滑动到底部监听器
     */
    public interface WebViewScrollBottomListener {
        /**
         * 滑动到底部回调
         */
        void onScrollBottom();
    }
}
