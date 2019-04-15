package com.din.thedialog.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import com.din.thedialog.R;

public class LoadProgress extends View {

    /*
     * 进度条的总高度，即整个View的高度（可通过xml的属性设置）
     */
    private int height;

    /*
     * 当前已完成的进度值
     */
    private int value = 0;

    /*
     * 总的进度数量（可通过xml的属性设置）
     */
    private int maxValue;

    /*
     * 显示的默认文字
     */
    private String text = "100%";

    /*
     * 百分比的文字大小（可通过xml的属性设置）
     */
    private int textSize;

    /*
     * 文字区域的宽度
     */
    private int textWidth;

    /*
     * 百分比文字是否静止在末尾（可通过xml的属性设置）
     */
    private boolean isStatic;

    /*
     * 进度条显示的高度（可通过xml的属性设置）
     */
    private int lineHeight;

    /*
     * 已加载的进度条的颜色（可通过xml的属性设置）
     */
    private int color;

    /*
     * 加载监听事件
     */
    private OnLoadListener onLoadListener;

    private Paint mPaint;                   // 绘制背景灰色线条画笔
    private Paint mPaintText;               // 绘制下载进度画笔
    private Rect mBound;                    // 获取百分比数字的长宽
    private float paddingLeft, paddingRight;// 文字与文字区域的padding值

    public LoadProgress(Context context) {
        this(context, null);
    }

    public LoadProgress(Context context, AttributeSet attribute) {
        this(context, attribute, 0);
    }

    public LoadProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LoadProgress);     // 获取自定义属性

        height = (int) t.getDimension(R.styleable.LoadProgress_height, dip(40));
        lineHeight = (int) t.getDimension(R.styleable.LoadProgress_lineHeight, dip(4));
        textSize = (int) t.getDimension(R.styleable.LoadProgress_textSize, 36);
        maxValue = t.getInteger(R.styleable.LoadProgress_maxValue, 100);
        color = t.getColor(R.styleable.LoadProgress_loadColor, getResources().getColor(android.R.color.holo_blue_light));
        isStatic = t.getBoolean(R.styleable.LoadProgress_isStatic, false);
        textWidth = getTextWidth();
    }

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBound = new Rect();
        paddingLeft = dip(2);
        paddingRight = dip(2);
    }

    /**
     * 设置当前进度值
     * @param currentValue
     */
    public LoadProgress setValue(int currentValue) {
        this.value = currentValue;
        invalidate();
        return this;
    }

    public LoadProgress setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintText.setColor(color);                                         // 设置绘制百分比文字
        mPaintText.setTextSize(textSize);
        text = value + "%";                                                 // 根据文字的位置设置文字的Rect大小
        mPaintText.getTextBounds(text, 0, text.length(), mBound);

        textWidth = mBound.width();
        int lineOffsetTop = (getHeight() - lineHeight) / 2;                 // 距离顶部偏移量
        float lineWidth;                                                    // 已进行的进度的宽度
        float darkLineWidth;                                                // 灰色进度条的宽度
        float textStart;                                                    // 文字起始点
        float whiteBgStart, whiteBgStop;                                    // 文字背景区域起始点，与文字之间有个padding值

        if (isStatic) {
            darkLineWidth = getWidth() - textWidth - paddingLeft - paddingRight;
            lineWidth = darkLineWidth * value / maxValue;
            textStart = getWidth() - textWidth - paddingRight;
            whiteBgStart = darkLineWidth;
            whiteBgStop = getWidth();
        } else {
            darkLineWidth = getWidth();
            lineWidth = (getWidth() - textWidth - paddingLeft - paddingRight) * value / maxValue;
            textStart = lineWidth + paddingLeft;
            whiteBgStart = lineWidth;
            whiteBgStop = lineWidth + textWidth + paddingLeft + paddingRight;
        }

        mPaint.setColor(getResources().getColor(R.color.colorGray));        // 设置绘制底色
        mPaint.setStrokeWidth(lineHeight);
        canvas.drawLine(0, lineOffsetTop, darkLineWidth, lineOffsetTop, mPaint);

        mPaint.setColor(color);                                             // 设置绘制进度条颜色
        mPaint.setStrokeWidth(lineHeight);
        canvas.drawLine(0, lineOffsetTop, lineWidth, lineOffsetTop, mPaint);

        mPaint.setColor(Color.WHITE);                                       // 设置绘制百分比文字的背景白色区域
        mPaint.setStrokeWidth(lineHeight);
        canvas.drawLine(whiteBgStart, lineOffsetTop, whiteBgStop, lineOffsetTop, mPaint);

        canvas.drawText(text, textStart, lineOffsetTop + mBound.height() / 2, mPaintText);        // 绘制文字

        if (value >= maxValue) {                                            // 判断是否加载完成
            onLoadListener.onFinished();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = measureHeight(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 计算需要的宽度
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {
        int width = defaultWidth;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            width = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            width = Math.max(defaultWidth, specSize);
        }
        return width;
    }

    /**
     * 计算需要的高度
     * @param defaultHeight
     * @param measureSpec
     * @return
     */
    private int measureHeight(int defaultHeight, int measureSpec) {
        int height = defaultHeight;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            height = this.height;
        } else if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            height = Math.max(defaultHeight, specSize);
        }
        return height;
    }

    /**
     * 获取文字的宽度
     */
    private int getTextWidth() {
        mPaintText.setTextSize(textSize);
        mPaintText.getTextBounds(text, 0, text.length(), mBound);
        return mBound.width() + 4;
    }

    /**
     * 转化为dip值
     * @param value
     * @return
     */
    private int dip(int value) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    public interface OnLoadListener {
        void onFinished();
    }
}