package com.berchina.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by zx on 2017/5/25 19:59
 * 项目名称：ProgressBar
 * 类描述：
 * 备注
 */
public class HorizontalProgressBar extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final String DEFAULT_TEXT_COLOR = "#ffffe103";
    private static final int DEFAULT_LEFT_HEIGHT = 2;
    private static final String DEFAULT_LEFT_COLOR = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_RIGHT_HEIGHT = 2;
    private static final String DEFAULT_RIGHT_COLOR = "#ffff0000";
    private static final int DEFAULT_TEXT_OFFSET = 4;

    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = Color.parseColor(DEFAULT_TEXT_COLOR);
    protected int mLeftHeight = dp2px(DEFAULT_LEFT_HEIGHT);
    protected int mLeftColor = Color.parseColor(DEFAULT_LEFT_COLOR);
    protected int mRightHeight = dp2px(DEFAULT_RIGHT_HEIGHT);
    protected int mRightColor = Color.parseColor(DEFAULT_RIGHT_COLOR);
    protected int mTextOffSet = dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint;
    //真实长度
    protected int mRealLength;


    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        init(attrs);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        //真实长度 为测量长度减去两边的padding值
        mRealLength = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //移动坐标
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        //绘制左边
        //默认右侧需要绘制
        boolean noNeedRight = false;
        //获取现在的值
        float radio = getProgress() * 1.0f / getMax();
        String text = getProgress() + "%";
        //测量字体长度
        int textWidth = (int) mPaint.measureText(text);
        float progressX = radio * mRealLength + mTextOffSet / 2;
        //如果前面的+字体长度大于了真实长度，说明后面的不用绘制了
        if (progressX + textWidth > mRealLength) {
            noNeedRight = true;
            progressX = mRealLength - textWidth;
        }
        //前面的长度为 真实长度*百分比-字体前边的空白
        float endX = progressX - mTextOffSet / 2;
        if (endX > 0) {
            mPaint.setColor(mLeftColor);
            mPaint.setStrokeWidth(mLeftHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        //绘制文字
        mPaint.setColor(mTextColor);
        int y = (int) -((mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        //绘制右边
        if (!noNeedRight) {
            mPaint.setColor(mRightColor);
            mPaint.setStrokeWidth(mRightHeight);
            float startX = progressX + mTextOffSet / 2 + textWidth;
            canvas.drawLine(startX, 0, mRealLength, 0, mPaint);
        }
        //还原
        canvas.restore();
    }

    /**
     * 测量高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHight(int heightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {//用户给了明确的值
            result = height;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            //获得最大高度
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mLeftHeight, mRightHeight), textHeight);
            if (heightMode == MeasureSpec.AT_MOST) {//测量值不能超过设置值
                result = Math.min(height, result);
            }
        }
        return result;
    }

    /**
     * 初始化
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.HorizontalProgressBar);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBar_text_size, mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressBar_text_color, mTextColor);
        mLeftColor = ta.getColor(R.styleable.HorizontalProgressBar_left_color, mLeftColor);
        mLeftHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_left_height, mLeftHeight);
        mRightColor = ta.getColor(R.styleable.HorizontalProgressBar_right_color, mRightColor);
        mRightHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_right_height, mRightHeight);
        mTextOffSet = (int) ta.getDimension(R.styleable.HorizontalProgressBar_text_offset, mTextOffSet);
        ta.recycle();

        mPaint.setTextSize(mTextSize);
    }


    protected int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
