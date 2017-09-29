package xxx.test.allapplication.custom.swipemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import xxx.test.allapplication.R;

/**
 * Created by Yan Zhenjie on 2016/7/27.
 */
public class SwipeMenuLayout extends FrameLayout implements SwipeSwitch {

    public static final int DEFAULT_SCROLLER_DURATION = 200;

    private int mLeftViewId = 0; //左边菜单布局id
    private int mContentViewId = 0;//中间内容布局id
    private int mRightViewId = 0;//右边菜单布局id

    private float mOpenPercent = 0.5f;
    private int mScrollerDuration = DEFAULT_SCROLLER_DURATION;//打开菜单的时间

    private int mScaledTouchSlop;
    private int mLastX;//记录上一次x
    private int mLastY;//记录上一次y
    private int mDownX;//down初始值
    private int mDownY;//down初始值
    private View mContentView;
    private SwipeLeftHorizontal mSwipeLeftHorizontal;//在onFinishInflate初始化
    private SwipeRightHorizontal mSwipeRightHorizontal;//在onFinishInflate初始化
    private SwipeHorizontal mSwipeCurrentHorizontal;
    private boolean shouldResetSwipe;
    private boolean mDragging;
    private boolean swipeEnable = true;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mScaledMinimumFlingVelocity;//系统最小加速度
    private int mScaledMaximumFlingVelocity;//系统最大加速度


    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.recycler_swipe_SwipeMenuLayout);
        mLeftViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_leftViewId, mLeftViewId);
        mContentViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_contentViewId, mContentViewId);
        mRightViewId = typedArray.getResourceId(R.styleable.recycler_swipe_SwipeMenuLayout_rightViewId, mRightViewId);
        typedArray.recycle();

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mScaledTouchSlop = configuration.getScaledTouchSlop();
        mScaledMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        mScroller = new OverScroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mLeftViewId != 0 && mSwipeLeftHorizontal == null) {
            View view = findViewById(mLeftViewId);//左边菜单的view
            mSwipeLeftHorizontal = new SwipeLeftHorizontal(view);
        }
        if (mRightViewId != 0 && mSwipeRightHorizontal == null) {
            View view = findViewById(mRightViewId);
            mSwipeRightHorizontal = new SwipeRightHorizontal(view);
        }
        if (mContentViewId != 0 && mContentView == null) {
            mContentView = findViewById(mContentViewId);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(16);
            errorView.setText("You may not have set the ContentView.");
            mContentView = errorView;
            addView(mContentView);
        }
    }

    /**
     * Set whether open swipe. Default is true.
     *
     * @param swipeEnable true open, otherwise false.
     */
    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    /**
     * Open the swipe function of the Item?
     *
     * @return open is true, otherwise is false.
     */
    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    /**
     * Set open percentage.
     *
     * @param openPercent such as 0.5F.
     */
    public void setOpenPercent(float openPercent) {
        this.mOpenPercent = openPercent;
    }

    /**
     * Get open percentage.
     *
     * @return such as 0.5F.
     */
    public float getOpenPercent() {
        return mOpenPercent;
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such as 500.
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mScrollerDuration = scrollerDuration;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);//先拿到父类是否拦截的值
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = mLastX = (int) ev.getX();
                mDownY  = mLastY = (int) ev.getY();
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                int disX = (int) (ev.getX() - mDownX);
                int disY = (int) (ev.getY() - mDownY);
                return Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY);//返回是否是水平滑动
            }
            case MotionEvent.ACTION_UP: {
                Log.i("neo","up = "+(int) ev.getX());
                boolean isClick = mSwipeCurrentHorizontal != null
                        && mSwipeCurrentHorizontal.isClickOnContentView(getWidth(), ev.getX());
                if (isMenuOpen() && isClick) {
                    smoothCloseMenu();
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                return false;
            }
        }
        return isIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(ev);//跟踪滑动速度,固定代码
        int dx;
        int dy;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {//这里一般情况不会执行  因为onInterceptTouchEvent的DOWN返回false
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isSwipeEnable()) break;
                int disX = (int) (mLastX - ev.getX());
                int disY = (int) (mLastY - ev.getY());
                if (!mDragging && Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY)) {
                    mDragging = true;//水平滑动开始  设置标志位
                }
                if (mDragging) {
                    if (mSwipeCurrentHorizontal == null || shouldResetSwipe) {//最开始mSwipeCurrentHorizontal为null
                        if (disX < 0) {//向右滑 显示左菜单
                            if (mSwipeLeftHorizontal != null)
                                mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
                            else
                                mSwipeCurrentHorizontal = mSwipeRightHorizontal;
                        } else {//左滑 显示右菜单
                            if (mSwipeRightHorizontal != null)
                                mSwipeCurrentHorizontal = mSwipeRightHorizontal;
                            else
                                mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
                        }
                    }
                    scrollBy(disX, 0);//使内容滑动
                    mLastX = (int) ev.getX();//更新值
                    mLastY = (int) ev.getY();//更新值
                    shouldResetSwipe = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                dx = (int) (mDownX - ev.getX());
                dy = (int) (mDownY - ev.getY());
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);//计算当前的速度,单位为 px/s  1s多少像素
                int velocityX = (int) mVelocityTracker.getXVelocity();//水平方向滑动速度
                int velocity = Math.abs(velocityX);
                if (velocity > mScaledMinimumFlingVelocity) {//速度大于系统最小
                    if (mSwipeCurrentHorizontal != null) {
                        int duration = getSwipeDuration(ev, velocity);
                        if (mSwipeCurrentHorizontal instanceof SwipeRightHorizontal) {//如果有右菜单
                            if (velocityX < 0) {//左滑
                                smoothOpenMenu(duration);
                            } else {//右滑
                                smoothCloseMenu(duration);
                            }
                        } else {////如果有左菜单
                            if (velocityX > 0) {//右滑
                                smoothOpenMenu(duration);
                            } else {//左滑
                                smoothCloseMenu(duration);
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                } else {
                    judgeOpenClose(dx, dy);
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (Math.abs(mDownX - ev.getX()) > mScaledTouchSlop
                        || Math.abs(mDownY - ev.getY()) > mScaledTouchSlop
                        || isLeftMenuOpen()
                        || isRightMenuOpen()) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                } else {
                    dx = (int) (mDownX - ev.getX());
                    dy = (int) (mDownY - ev.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * compute finish duration.
     *
     * @param ev       up event.
     * @param velocity velocity x.
     * @return finish duration.
     */
    private int getSwipeDuration(MotionEvent ev, int velocity) {
        int sx = getScrollX();// 获取x轴起始位置
        int dx = (int) (ev.getX() - sx);//计算x方向上移动的距离
        final int width = mSwipeCurrentHorizontal.getMenuWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);////要移动的距离占宽度的比例，这个比例必须得小于等于1
        final float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {//如果是手指滑动，则需要根据手指滑动速度计算滑动持续时间
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {//如果手指滑动速度为0，即，是通过代码的方式滑动到指定位置，则使用另一种方式计算滑动持续时间
            final float pageDelta = (float) Math.abs(dx) / width;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, mScrollerDuration);
        return duration;
    }
    //实现变速滑动
    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    private void judgeOpenClose(int dx, int dy) {
        if (mSwipeCurrentHorizontal != null) {
            if (Math.abs(getScrollX()) >= (mSwipeCurrentHorizontal.getMenuView().getWidth() * mOpenPercent)) { // auto open
                if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual()) smoothCloseMenu();
                    else smoothOpenMenu();
                } else { // normal up
                    if (isMenuOpen()) smoothCloseMenu();
                    else smoothOpenMenu();
                }
            } else { // auto closeMenu
                smoothCloseMenu();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mSwipeCurrentHorizontal == null) {
            super.scrollTo(x, y);
        } else {
            SwipeHorizontal.Checker checker = mSwipeCurrentHorizontal.checkXY(x, y);//处理边界问题
            shouldResetSwipe = checker.shouldResetSwipe;//x = 0 时为true
            if (checker.x != getScrollX()) {//处理边界问题!!!
                super.scrollTo(checker.x, checker.y);
            }
        }
    }

    @Override
    public void computeScroll() {//和mScroller.startScroll配置使用 弹性滑动 固定代码
        if (mScroller.computeScrollOffset() && mSwipeCurrentHorizontal != null) {
            if (mSwipeCurrentHorizontal instanceof SwipeRightHorizontal) {
                scrollTo(Math.abs(mScroller.getCurrX()), 0);
                invalidate();
            } else {
                scrollTo(-Math.abs(mScroller.getCurrX()), 0);
                invalidate();
            }
        }
    }

    public boolean hasLeftMenu() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.canSwipe();
    }

    public boolean hasRightMenu() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.canSwipe();
    }

    @Override
    public boolean isMenuOpen() {
        return isLeftMenuOpen() || isRightMenuOpen();
    }

    @Override
    public boolean isLeftMenuOpen() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.isMenuOpen(getScrollX());
    }

    @Override
    public boolean isRightMenuOpen() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.isMenuOpen(getScrollX());
    }

    @Override
    public boolean isCompleteOpen() {
        return isLeftCompleteOpen() || isRightMenuOpen();
    }

    @Override
    public boolean isLeftCompleteOpen() {
        return mSwipeLeftHorizontal != null && !mSwipeLeftHorizontal.isCompleteClose(getScrollX());
    }

    @Override
    public boolean isRightCompleteOpen() {
        return mSwipeRightHorizontal != null && !mSwipeRightHorizontal.isCompleteClose(getScrollX());
    }

    @Override
    public boolean isMenuOpenNotEqual() {
        return isLeftMenuOpenNotEqual() || isRightMenuOpenNotEqual();
    }

    @Override
    public boolean isLeftMenuOpenNotEqual() {
        return mSwipeLeftHorizontal != null && mSwipeLeftHorizontal.isMenuOpenNotEqual(getScrollX());
    }

    @Override
    public boolean isRightMenuOpenNotEqual() {
        return mSwipeRightHorizontal != null && mSwipeRightHorizontal.isMenuOpenNotEqual(getScrollX());
    }

    @Override
    public void smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenLeftMenu() {
        smoothOpenLeftMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenRightMenu() {
        smoothOpenRightMenu(mScrollerDuration);
    }

    @Override
    public void smoothOpenLeftMenu(int duration) {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
            smoothOpenMenu(duration);
        }
    }

    @Override
    public void smoothOpenRightMenu(int duration) {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal;
            smoothOpenMenu(duration);
        }
    }

    /**
     * 平滑的打开菜单
     */
    private void smoothOpenMenu(int duration) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal.autoOpenMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    @Override
    public void smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration);
    }

    @Override
    public void smoothCloseLeftMenu() {
        if (mSwipeLeftHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeLeftHorizontal;
            smoothCloseMenu();
        }
    }

    @Override
    public void smoothCloseRightMenu() {
        if (mSwipeRightHorizontal != null) {
            mSwipeCurrentHorizontal = mSwipeRightHorizontal;
            smoothCloseMenu();
        }
    }

    @Override
    public void smoothCloseMenu(int duration) {
        if (mSwipeCurrentHorizontal != null) {
            mSwipeCurrentHorizontal.autoCloseMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int contentViewHeight = 0;

        if (mContentView != null) {
            measureChildWithMargins(mContentView, widthMeasureSpec, 0, heightMeasureSpec, 0);//测量mContentView,中间的布局
            contentViewHeight = mContentView.getMeasuredHeight();
        }

        if (mSwipeLeftHorizontal != null) {
            View leftMenu = mSwipeLeftHorizontal.getMenuView();
            int menuViewHeight = contentViewHeight == 0 ? leftMenu.getMeasuredHeightAndState() : contentViewHeight;

            int menuWidthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
            int menuHeightSpec = MeasureSpec.makeMeasureSpec(menuViewHeight, MeasureSpec.EXACTLY);
            leftMenu.measure(menuWidthSpec, menuHeightSpec);
        }

        if (mSwipeRightHorizontal != null) {
            View rightMenu = mSwipeRightHorizontal.getMenuView();
            int menuViewHeight = contentViewHeight == 0 ? rightMenu.getMeasuredHeightAndState() : contentViewHeight;

            int menuWidthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
            int menuHeightSpec = MeasureSpec.makeMeasureSpec(menuViewHeight, MeasureSpec.EXACTLY);
            rightMenu.measure(menuWidthSpec, menuHeightSpec);
        }

        if (contentViewHeight > 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), contentViewHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int contentViewHeight;
        if (mContentView != null) {
            int contentViewWidth = mContentView.getMeasuredWidthAndState();
            contentViewHeight = mContentView.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
            int start = getPaddingLeft();
            int top = getPaddingTop() + lp.topMargin;
            mContentView.layout(start, top, start + contentViewWidth, top + contentViewHeight);
        }

        if (mSwipeLeftHorizontal != null) {
            View leftMenu = mSwipeLeftHorizontal.getMenuView();
            int menuViewWidth = leftMenu.getMeasuredWidthAndState();
            int menuViewHeight = leftMenu.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) leftMenu.getLayoutParams();
            int top = getPaddingTop() + lp.topMargin;
            leftMenu.layout(-menuViewWidth, top, 0, top + menuViewHeight);
        }

        if (mSwipeRightHorizontal != null) {
            View rightMenu = mSwipeRightHorizontal.getMenuView();
            int menuViewWidth = rightMenu.getMeasuredWidthAndState();
            int menuViewHeight = rightMenu.getMeasuredHeightAndState();
            LayoutParams lp = (LayoutParams) rightMenu.getLayoutParams();
            int top = getPaddingTop() + lp.topMargin;

            int parentViewWidth = getMeasuredWidthAndState();
            rightMenu.layout(parentViewWidth, top, parentViewWidth + menuViewWidth, top + menuViewHeight);
        }
    }

}