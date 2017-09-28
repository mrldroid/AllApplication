package xxx.test.allapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by neo on 16/12/26.
 */

public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("neo","width mode = "+(MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.EXACTLY?"EXACTLY":MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.AT_MOST?"AT_MOST":"UnDefine")+"  size = "+MeasureSpec.getSize(widthMeasureSpec));
        Log.i("neo","height mode = "+(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.EXACTLY?"EXACTLY":MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.AT_MOST?"AT_MOST":"UnDefine")+"  size = "+MeasureSpec.getSize(heightMeasureSpec));

        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    float lastX = 0,lastY = 0;
    Scroller mScroller = new Scroller(getContext());
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x,y;
        x = event.getRawX();
        y = event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("neo","getY = " +event.getY());
                Log.i("neo","getRawY = " +event.getRawY());
                lastX = x ;
                lastY = y ;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = x - lastX;
                float offsetY = y - lastY;
                //layout((int) (getLeft()+offsetX),(int) (getTop()+offsetY),(int) (getRight()+offsetX),(int) (getBottom()+offsetY));
                ((View) getParent()).scrollBy(-(int)offsetX, -(int)offsetY);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                View view = (View) getParent();
                mScroller.startScroll(view.getScrollX(),view.getScrollY(),-view.getScrollX(),-view.getScrollY());
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){//返回true则表示还未到达目标位置,即未完成滑动
            ((View)getParent()).scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }
}
