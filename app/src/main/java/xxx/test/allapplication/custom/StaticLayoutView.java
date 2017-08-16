package xxx.test.allapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import xxx.test.allapplication.R;

public class StaticLayoutView extends View {

    private Layout layout = null;
    private ClickableSpan mPressedSpan;

    public StaticLayoutView(Context context) {
        this(context, null);
    }

    public StaticLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaticLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        if (layout != null) {
            canvas.translate(getPaddingLeft(), getPaddingTop());//使得左上角坐标为0,0
            layout.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (layout != null) {
            setMeasuredDimension(layout.getWidth(), layout.getHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (layout == null) {
            return super.onTouchEvent(event);
        }
        Spannable spannable = layout.getText() instanceof Spannable ? (Spannable) layout.getText() : null;
        try {
            if (spannable != null) {
                int action = event.getAction();

                switch (action){
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_DOWN:
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        x -= getPaddingLeft();
                        y -= getPaddingTop();

                        x += getScrollX();
                        y += getScrollY();

                        Layout layout = this.layout;
                        int line = layout.getLineForVertical(y);//得到触摸点所在TextView的行数
                        int offset = layout.getOffsetForHorizontal(line, x);//得到在该行的文字的位置

                        ClickableSpan[] link = spannable.getSpans(offset, offset, ClickableSpan.class);//返回指定片段的样式

                        if (link.length != 0) {
                            mPressedSpan = link[0];
                            if (action == MotionEvent.ACTION_UP) {
                                mPressedSpan.onClick(this);
                                reset(spannable);
                            } else if (action == MotionEvent.ACTION_DOWN) {
                                spannable.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorGold)), spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                invalidate();
                            }
                            return true;
                        } else {
                            reset(spannable);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        ClickableSpan touchedSpan = getPressedSpan(this, spannable, event);
                        if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                            reset(spannable);
                        }
                        break;
                    default:
                        reset(spannable);
                        break;
                }

            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

    private void reset(Spannable spannable) {
        spannable.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), spannable.getSpanStart(mPressedSpan), spannable.getSpanEnd(mPressedSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        invalidate();
        Selection.removeSelection(spannable);
    }

    private ClickableSpan getPressedSpan(StaticLayoutView staticLayoutView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= staticLayoutView.getPaddingLeft();
        y -= staticLayoutView.getPaddingTop();

        x += staticLayoutView.getScrollX();
        y += staticLayoutView.getScrollY();

        Layout layout = staticLayoutView.layout;
        int line = layout.getLineForVertical(y);
        int offset = layout.getOffsetForHorizontal(line, x);

        ClickableSpan[] link = spannable.getSpans(offset, offset, ClickableSpan.class);
        ClickableSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }
}