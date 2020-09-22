package com.microink.clandroid.android.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.microink.clandroid.R;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 11:35 AM
 *
 * TextView with stroke
 * 带描边的TextView
 */
public class StrokeTextView extends AppCompatTextView {

    private Paint strokePaint;
    /**
     * Stroke color
     * 描边颜色
     */
    private int strokeColor;
    /**
     * Stroke thickness
     * 描边粗细
     */
    private float strokeWidth;

    public StrokeTextView(Context context) {
        super(context);

        init(context, null);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
            strokeColor = typedArray.getColor(R.styleable.StrokeTextView_cl_strokeColor, Color.BLACK);
            strokeWidth = typedArray.getDimension(R.styleable.StrokeTextView_cl_strokeWidth, 0);

            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (strokePaint == null) {
            strokePaint = new TextPaint();
        }
        // 复制原来TextView画笔中的一些参数
        TextPaint paint = getPaint();
        strokePaint.setTextSize(paint.getTextSize());
        strokePaint.setTypeface(paint.getTypeface());
        strokePaint.setFlags(paint.getFlags());
        strokePaint.setAlpha(paint.getAlpha());

        // 自定义描边效果
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(strokeWidth);

        String text = getText().toString();
        //在文本底层画出带描边的文本
        canvas.drawText(text, (getWidth() - strokePaint.measureText(text)) / 2,
                getBaseline(), strokePaint);
        super.onDraw(canvas);
    }
}
