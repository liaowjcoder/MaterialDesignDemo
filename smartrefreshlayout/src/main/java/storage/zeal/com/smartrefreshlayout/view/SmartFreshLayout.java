package storage.zeal.com.smartrefreshlayout.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Px;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by liaowj on 2017/5/11.
 */

public class SmartFreshLayout extends ViewGroup implements NestedScrollingParent {

    private final NestedScrollingParentHelper parentHelper;
    private TextView headerView;
    private RecyclerView mScrollView;
    private int headHeight;
    private int refreshHeight;

    private Scroller mScroller;

    private static final int NONE = 0;
    private static final int REFRESHING = 1;
    private static final int RELEASE_REFRESH = 2;
    private static final int DRAG_DOWN = 3;
    private int mCurrentState = NONE;

    private static final float DUMP = 0.25f;


    public SmartFreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.CYAN);
        parentHelper = new NestedScrollingParentHelper(this);
        init();
    }

    private void init() {
        mScroller = new Scroller(this.getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        headerView = (TextView) getChildAt(0);

        //测量头部
        measureChild(headerView, widthMeasureSpec, heightMeasureSpec);

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);

        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int usedHeight = parentHeightSize - headerView.getMeasuredHeight();


        mScrollView = (RecyclerView) getChildAt(1);


        measureChildWithMargins(mScrollView, widthMeasureSpec, 0, heightMeasureSpec, usedHeight);


        setMeasuredDimension(parentWidthSize, parentHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        headerView.layout(0, -headerView.getMeasuredHeight(),
                getMeasuredWidth(), 0);

        mScrollView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());


        headHeight = dip2Px(100);

        refreshHeight = headHeight / 2;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //解决还没滑动结束又开始第二次滑动出现滑动跳跃问题
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        //头部可见
        if (getScrollY() < 0) {
            if (mCurrentState == DRAG_DOWN) {
                Log.e("zeal","000000");
                startScroll(getScrollX(), -getScrollY());
                reset();
            } else if (mCurrentState == RELEASE_REFRESH) {
                //开始刷新
                if (Math.abs(getScrollY()) > refreshHeight) {
                    Log.e("zeal","11111");
                    mCurrentState = REFRESHING;
                    headerView.setText("正在刷新");
                    startScroll(0, -getScrollY() - refreshHeight);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                        }
                    }, 3000);
                }
            }
//            if (Math.abs(getScrollY()) > refreshHeight) {
//                mCurrentState = REFRESHING;
//                startScroll(0, -getScrollY() - refreshHeight);
//                headerView.setText("正在刷新");
////                post(new Runnable() {
////                    @Override
////                    public void run() {
//////                        startScroll(0, -getScrollY());
////                        reset();
////                    }
////                });
//            }
        }
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {


        if (mCurrentState == REFRESHING) {
            //正在就不要动
            consumed[1] = dy;
//            dy = (int) (dy * DUMP * DUMP);
            //Log.e("zeal", "dy:" + dy);
//            dy = dy < 0 ? -3 : 3;
//            int y = dy;
            scrollBy(0, dy);
            return;
        }


        if (dy < 0) {
            //手指往下滑动
            //判断rv是否能往下滑动
            //positive to check scrolling down
            boolean canScrollVertically = ViewCompat.canScrollVertically(mScrollView, -1);
            //Log.e("zeal", "能下滑吗？" + canScrollVertically);
            if (!canScrollVertically) {

                consumed[1] = dy;

                scrollTo(getScrollX(), getScrollY() + dy);
            }
        } else {
            //手指向上滑动

            //判断当前的头部是否显示出来了
            if (getScrollY() < 0) {
                consumed[1] = dy;
                scrollTo(getScrollX(), getScrollY() + dy);
            }
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public SmartFreshLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SmartFreshLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

//    @Override
//    public void scrollTo(@Px int x, @Px int y) {

//        if (mCurrentState != NONE) {
//            if (mCurrentState == REFRESHING) {
//                if (y < getScrollY()) {
////                    Log.e("zeal", "----------:" + y);
//                    y = (int) (y * DUMP);
//                }
//            } else {
//                y = (int) (y * DUMP);
//            }
//        }


//        if (y > headHeight) {
//            y = headHeight;
//        }
//
//        if (y < -headHeight) {
//            y = -headHeight;
//        }
//        if (y != getScrollY()) {
//            super.scrollTo(x, y);
//        }


//    }


    private void changeStateText() {
        //改变文本
        if (mCurrentState != REFRESHING) {
            if (Math.abs(getScrollY()) < refreshHeight && mCurrentState != REFRESHING) {
                headerView.setText("下拉刷新");
                mCurrentState = DRAG_DOWN;
            } else {
                mCurrentState = RELEASE_REFRESH;
                headerView.setText("松开刷新");
            }
        }
    }


    private void startScroll(int dx, int dy) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 2000);
        invalidate();
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

    }

    /**
     * 重置
     */
    private void reset() {
        mCurrentState = NONE;
        startScroll(0, -getScrollY());
    }

    public int dip2Px(int dip) {
        // px/dp = density;
        // px和dp比例关系
        float density = getResources().getDisplayMetrics().density;

        int px = (int) (density * dip + .5f);
        return px;
    }
}
