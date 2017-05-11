package com.zeal.com.sticknavlayoutdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;


import com.zeal.com.sticknavlayoutdemo.R;

/**
 * Created by liaowj on 2017/5/2.
 */

public class StickNavLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "StickNavLayout";

    private OverScroller mScroller;

    private View mNav;
    private ViewPager mViewPager;
    private int mTopViewHeight;
    private View mTop;

    private final NestedScrollingParentHelper mParentHelper;

    public StickNavLayout(Context context) {
        this(context, null);

    }

    public StickNavLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickNavLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mParentHelper = new NestedScrollingParentHelper(this);

        init();
    }


    private void init() {

        mScroller = new OverScroller(this.getContext());
    }

    //------------------measure------------------
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //调用父 View 的测量规则
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //不限制第一个孩子的高度
        getChildAt(0).measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mNav.getMeasuredHeight()/*1640*/;
        mViewPager.setLayoutParams(params);

        Log.e(TAG, "getMeasuredHeight()：" + getMeasuredHeight());
        Log.e(TAG, "params.height：" + (getMeasuredHeight() - mNav.getMeasuredHeight()));
        Log.e(TAG, "测量总高度：" + (mTop.getMeasuredHeight() + mNav.getMeasuredHeight() +
                mViewPager.getMeasuredHeight()));
        setMeasuredDimension(getMeasuredWidth(), mTop.getMeasuredHeight() + getMeasuredHeight()
        );

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTop = findViewById(R.id.id_stickynavlayout_topview);
        mNav = findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y < 0) {
            y = 0;
        }

        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private void flinling(int velocityY) {

        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();

    }

    //------------------nestedparent------------------

    /**
     * @param child            当前类的的直接子类
     * @param target           触发 startNestScroll 的目标资料
     * @param nestedScrollAxes
     * @return
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e(TAG, "onStartNestedScroll child:" + child);
        Log.e(TAG, "onStartNestedScroll target:" + target);
        //判断当前孩子传递过来的 nestedScrollAxes 是否为竖直方向上的滑动
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        //super.onNestedScrollAccepted(child, target, nestedScrollAxes);
        Log.e(TAG, "onNestedScrollAccepted");
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        //判断当前 View 的 topView 的状态
        boolean hiden = dy > 0 && getScrollY() < mTopViewHeight;
        boolean show = !ViewCompat.canScrollVertically(target, -1) && dy < 0 && getScrollY() >= 0;

        if (hiden || show) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
        Log.e(TAG, "onNestedPreScroll");
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (getScrollY() >= mTopViewHeight) {
            //不消费当前的 finling 事件
            return false;
        } else {
            flinling((int) velocityY);
            return true;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
        mParentHelper.onStopNestedScroll(child);
        Log.e(TAG, "onStopNestedScroll");
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }
}
