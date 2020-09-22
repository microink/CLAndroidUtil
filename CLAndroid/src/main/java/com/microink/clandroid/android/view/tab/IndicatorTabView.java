package com.microink.clandroid.android.view.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.microink.clandroid.R;
import com.microink.clandroid.android.color.ColorUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 11:41 AM
 *
 * Tab displays controls, have indicators, supports only one line of equal width,
 * With the ViewPager percentage move has a color gradient selected effect
 * Tab显示控件，带有指示器，仅支持一行等比宽度，
 * 配合ViewPager百分比移动有颜色渐变选中效果
 */
public class IndicatorTabView extends View {

    /**
     * Default text size
     * 默认文字大小
     */
    private static final int DEFAULT_TEXT_SIZE = 30;

    /**
     * Indicator paint
     * 指示器画笔
     */
    private Paint mIndicatorPaint;

    /**
     * Every tab width
     * 每个Tab宽度
     */
    private int tabItemWidth;
    /**
     * Tab indicator width
     * Tab指示器宽度
     */
    private int tabIndicatorWidth;
    /**
     * Tab indicator height
     * Tab指示器高度
     */
    private int tabIndicatorHeight;
    /**
     * Tab indicator color
     * Tab指示器颜色
     */
    private int tabIndicatorColor;
    /**
     * Tab text size
     * Tab文字大小
     */
    private float tabTextSize;
    /**
     * Tab text color
     * Tab文字颜色
     */
    private int tabTextColor;
    /**
     * Tab select tab text color
     * Tab选中文字颜色
     */
    private int tabTextSelectedColor;
    /**
     * View height
     * 整体布局高度
     */
    private int viewHeight;
    /**
     * Indicator radius size
     * 指示器圆角大小
     */
    private float indicatorRadius;

    /**
     * Tab text list
     * Tab文字列表
     */
    private List<String> tabList;
    /**
     * Current tab position
     * 当前Tab位置
     */
    private int currentPosition;
    /**
     * The offset percentage of the ViewPager slide comes from onPageScrolled
     * ViewPager滑动的偏移百分比 来自onPageScrolled
     */
    private float positionOffset;
    /**
     * The ViewPager slide listens for the callback position from onPageScrolled
     * ViewPager滑动的监听回调位置 来自onPageScrolled
     */
    private int scrollPosition;

    private OnTabClickListener tabClickListener;

    public IndicatorTabView(Context context) {
        super(context);

        initView(context, null);
    }

    public IndicatorTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public IndicatorTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    public IndicatorTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {

        if (null != attributeSet) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.IndicatorTabView);
            tabTextColor = typedArray.getColor(R.styleable.IndicatorTabView_cl_indicatorTextColor, 0);
            tabTextSelectedColor = typedArray.getColor(R.styleable.IndicatorTabView_cl_indicatorSelectedTextColor, 0);
            tabTextSize = typedArray.getDimensionPixelSize(R.styleable.IndicatorTabView_cl_indicatorTextSize, DEFAULT_TEXT_SIZE);
            tabIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.IndicatorTabView_cl_indicatorWidth, 0);
            tabIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.IndicatorTabView_cl_indicatorHeight, 0);
            indicatorRadius = tabIndicatorHeight / 2f;
            tabIndicatorColor = typedArray.getColor(R.styleable.IndicatorTabView_cl_indicatorColor, 0);

            typedArray.recycle();
        }

        tabList = new ArrayList<>();
        tabList.add("Example A");
        tabList.add("Example B");
        //tabTextSize = 30f;
        //tabIndicatorWidth = 30;
        //tabIndicatorHeight = 5;

        createIndicatorPaint();
        setClickable(true);
        clickHandle();
    }

    /**
     * 设置Tab列表文字
     * @param list
     */
    public void setTabList(List<String> list) {
        tabList.addAll(list);

        invalidate();
    }

    public void onPageScrolled(int position, float positionOffset) {
        this.positionOffset = positionOffset;
        scrollPosition = position;
    }

    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (null != tabList) {
            tabItemWidth = w / tabList.size();
        }
        viewHeight = h;
    }

    /**
     * 创建文本画笔
     */
    private Paint createTextPaint(int i) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(tabTextSize);
        //paint.setColor(i == currentPosition ? tabTextSelectedColor : tabTextColor);

        return paint;
    }

    /**
     * 创建指示器画笔
     * @return
     */
    private void createIndicatorPaint() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setColor(tabIndicatorColor);
    }

    /**
     * 点击事件处理
     */
    private void clickHandle() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        float x = event.getX();
                        for (int i = 0; i < tabList.size(); i++) {
                            float temp = i * tabItemWidth + 40 ;
                            if (x >= temp && x < temp + tabItemWidth) {
                                if (null != tabClickListener) {
                                    tabClickListener.onTabClick(i);
                                }
                                break;
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 设置点击事件监听
     * @param listener
     */
    public void setOnTabClickListener(OnTabClickListener listener) {
        tabClickListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < tabList.size(); i++) {
            // Draw text
            Paint paint = createTextPaint(i);
            String tabText = tabList.get(i);
            Rect rect = new Rect();
            paint.getTextBounds(tabText, 0, tabText.length(), rect);
            float textHeight = 0 + rect.bottom - rect.top;
            float textWidth = rect.right - rect.left;
            float textStartX = i * tabItemWidth + (tabItemWidth - textWidth) / 2;
            if (0 != positionOffset) {
                // In scroll
                if (i == scrollPosition) {
                    // Left Tab,The larger the positionOffset, the fewer Tab pages on the left
                    // 左侧Tab positionOffset越大，左侧Tab页越少
                    LinearGradient linearGradient = new LinearGradient(
                            textStartX, 0, textStartX + textWidth * positionOffset, 0,
                            ColorUtil.getCurrentColor(tabTextSelectedColor, tabTextColor,
                                    positionOffset),
                            tabTextSelectedColor,
                            Shader.TileMode.CLAMP);
                    paint.setShader(linearGradient);
                } else if (i == scrollPosition + 1) {
                    // Right Tab
                    LinearGradient linearGradient = new LinearGradient(
                            textStartX, 0, textStartX + textWidth * positionOffset, 0,
                            tabTextSelectedColor,
                            ColorUtil.getCurrentColor(tabTextSelectedColor, tabTextColor,
                                    1 - positionOffset),
                            Shader.TileMode.CLAMP);
                    paint.setShader(linearGradient);
                } else {
                    // Normal Tab
                    paint.setColor(tabTextColor);
                }
            } else {
                // Not in scroll
                paint.setColor(i == scrollPosition ? tabTextSelectedColor : tabTextColor);
            }

            canvas.drawText(tabText, textStartX,
                    textHeight, paint);

            // Draw indicator
            if (scrollPosition == i) {
                float nextDistanceX = tabItemWidth;
                int usePosition = scrollPosition;
                float moveX = positionOffset * nextDistanceX;

                float indicatorStartX = usePosition * tabItemWidth +
                        (tabItemWidth - tabIndicatorWidth) / 2f + moveX;
                float indicatorStartY = viewHeight - tabIndicatorHeight;

                canvas.drawRoundRect(indicatorStartX, indicatorStartY,
                        indicatorStartX + tabIndicatorWidth, viewHeight,
                        indicatorRadius, indicatorRadius,
                        mIndicatorPaint);
            }
        }
    }

    public interface OnTabClickListener {
        /**
         * Tab click callback
         * @param position Position
         */
        void onTabClick(int position);
    }
}