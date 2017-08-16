package xxx.test.allapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liujun on 17/4/19.
 */

public class RecordLineView extends View {
    private Paint mPaint;
    public RecordLineView(Context context) {
        super(context);
        init();
    }

    public RecordLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(getLeft()+10,getTop(),getLeft()+10,getTop()+50,mPaint);
    }
}
