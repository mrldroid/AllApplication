package xxx.test.allapplication.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import xxx.test.allapplication.R;

/**
 * Created by neo on 17/11/14.
 * 自适应外框TextView
 */

public class BorderTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;
    private int mWidth, mHeight;
    private int mBorderColor;
    private float mBorderStrokeWidth;
    private float mCornerRadius;
    private RectF mRectF;

    private static int DEFAULT_PADDING_HORIZONTAL = dip2px(6.5f);
    private static int DEFAULT_PADDING_VERTICAL = dip2px(2.5f);
    private static float DEFAULT_STROKE_WIDTH = dip2px(0.5f);
    private static int DEFAULT_CORNER = dip2px(1.5f);

    public BorderTextView(Context context) {
        this(context,null);
    }

    public BorderTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BorderTextView);
        mBorderStrokeWidth = typedArray.getFloat(R.styleable.BorderTextView_borderStrokeWidth,DEFAULT_STROKE_WIDTH);
        mBorderColor = typedArray.getColor(R.styleable.BorderTextView_borderColor,Color.BLACK);
        mCornerRadius = typedArray.getFloat(R.styleable.BorderTextView_cornerRadius,DEFAULT_CORNER);
        typedArray.recycle();
        mRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderStrokeWidth);

        setPadding(DEFAULT_PADDING_HORIZONTAL, DEFAULT_PADDING_VERTICAL, DEFAULT_PADDING_HORIZONTAL, DEFAULT_PADDING_VERTICAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRectF = new RectF(0.5f*mBorderStrokeWidth, 0.5f*mBorderStrokeWidth, mWidth - mBorderStrokeWidth, mHeight - mBorderStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius, mPaint);
    }

    private static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
        mPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
        invalidate();
    }

    public void setBorderStrokeWidth(float strokeWidth) {
        mBorderStrokeWidth = strokeWidth;
        mRectF = new RectF(0.5f*mBorderStrokeWidth, 0.5f*mBorderStrokeWidth, mWidth - mBorderStrokeWidth, mHeight - mBorderStrokeWidth);
        invalidate();
    }

}