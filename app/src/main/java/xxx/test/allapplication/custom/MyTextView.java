package xxx.test.allapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private int mTranslateX = 0;
    private int mTranslateY = 0;

    private boolean mAnimating = true;
    private OnAnimateFinished onAnimateFinished;
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0 || mViewHeight == 0) {
            mViewWidth = getMeasuredWidth();
            mViewHeight = getMeasuredHeight();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(-mViewWidth, -mViewHeight, mViewWidth, mViewHeight+300,
                        new int[] { 0xfff85d00, 0xffffffff, 0xfff85d00 },
                        new float[] { 0.1f, 0.3f, 0.2f}, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
//                mPaint.setShadowLayer(0,1,1,0xffffffff);
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimating && mGradientMatrix != null) {
            mTranslateX += mViewWidth / 10;
            mTranslateY += mViewHeight /10;
            if (mTranslateX >= 2* mViewWidth) {
                if(onAnimateFinished != null){
                    onAnimateFinished.onFinished();
                }
                return;
            }
            mGradientMatrix.setTranslate(mTranslateX, mTranslateY);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(50);
        }


    }

    public void setOnAnimateFinished(OnAnimateFinished onAnimateFinished) {
        this.onAnimateFinished = onAnimateFinished;
    }

    public interface OnAnimateFinished{
        void onFinished();
    }
}