package storage.zeal.com.smartrefreshlayout.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

import storage.zeal.com.smartrefreshlayout.Footer;
import storage.zeal.com.smartrefreshlayout.Header;

/**
 * Created by liaowj on 2017/5/11.
 */

public class SmartFreshLayout2 extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    private final NestedScrollingParentHelper parentHelper;

    private final NestedScrollingChildHelper childHelper;

    private ViewGroup mScrollView;


    private Scroller mScroller;


    public static final int DRAG_DOWN = 1;
    public static final int RELEASE_REFRESH = 2;
    public static final int REFRESHING = 3;
    public static final int REFRESHING_DONE = 4;


    public static final int LOADING = 5;
    public static final int RELEASE_LOADING = 6;
    public static final int DRAG_UP = 7;
    public static final int LOADING_DONE = 8;
    public static final int NONE = 100;

    private int mCurrentState = NONE;

    private static final float DUMP = 0.25f;
    private static final float DUMP2 = 0.05f;

    private boolean mIsSupportRefresh;
    private boolean mIsSupportLoadMore;

    private int mRefreshHeight;
    private int mLoadMoreHeight;

    private Header mHeader;
    private Footer mFooter;

    private RefreshListener mOnRefreshListener;
    private LoadMoreListener mOnLoadMoreListener;


    public SmartFreshLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.CYAN);
        parentHelper = new NestedScrollingParentHelper(this);

        childHelper = new NestedScrollingChildHelper(this);

        init();
    }

    private void init() {
        setNestedScrollingEnabled(true);
        mScroller = new Scroller(this.getContext());
    }


    /**
     * 布局填充完毕之后回调
     * 该方法是在 onMeasure 之前调用的
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE || childView.getVisibility() == View.INVISIBLE) {
                continue;
            }
            if (childView instanceof Header) {
                mHeader = (Header) childView;
            } else if (childView instanceof RecyclerView || childView instanceof NestedScrollView) {
                mScrollView = (ViewGroup) childView;
            }
            if (childView instanceof Footer) {
                mFooter = (Footer) childView;
            }
        }

        //判断是否支持刷新功能
        mIsSupportRefresh = mHeader != null;
        //判断是否支持上拉加载更多
        mIsSupportLoadMore = mFooter != null;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //测量头部
        if (mIsSupportRefresh) {
            measureChild(mHeader.getHeaderView(), widthMeasureSpec, heightMeasureSpec);
        }


        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);

        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int usedHeight = parentHeightSize - (mIsSupportRefresh ? mHeader.getHeaderView().getMeasuredHeight() : 0);

        measureChildWithMargins(mScrollView, widthMeasureSpec, 0, heightMeasureSpec, usedHeight);

        //测量底部
        if (mIsSupportLoadMore) {
            measureChild(mFooter.getLoadMoreView(), widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(parentWidthSize, parentHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mIsSupportRefresh) {
            mHeader.getHeaderView().layout(0, -mHeader.getHeaderView().getMeasuredHeight(),
                    getMeasuredWidth(), 0);
            mRefreshHeight = mHeader.getRefreshHeight();

            if (mRefreshHeight == 0) {
                throw new RuntimeException("refreshHeight should'n be null");
            }
        }
        mScrollView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mScrollView.setBackgroundColor(Color.RED);
        if (mIsSupportLoadMore) {
            mFooter.getLoadMoreView().layout(0, mScrollView.getMeasuredHeight(),
                    getMeasuredWidth(),
                    mScrollView.getMeasuredHeight() + mFooter.getLoadMoreView().getMeasuredHeight());

            mLoadMoreHeight = mFooter.getLoadHeight();

            if (mLoadMoreHeight == 0) {
                throw new RuntimeException("loadmoreHeight should'n be null");
            }
        }


    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //解决还没滑动结束又开始第二次滑动出现滑动跳跃问题
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mScroller.forceFinished(true);
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        parentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View child) {
        parentHelper.onStopNestedScroll(child);
        //头部可见
        if (getScrollY() < 0) {
            if (mCurrentState == DRAG_DOWN) {
                //重置状态
                reset();
            } else if (mCurrentState == RELEASE_REFRESH) {
                //开始刷新
                notifyStateChange(REFRESHING);
                startScroll(0, -getScrollY() - mRefreshHeight);
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            } else if (mCurrentState == REFRESHING) {
                //继续回到刷新的位置
                startScroll(0, -getScrollY() - mRefreshHeight);
            }
        } else if (getScrollY() > 0) {//加载更多可见
            if (mCurrentState == DRAG_UP) {
                //重置状态
                reset();
            } else if (mCurrentState == RELEASE_LOADING) {
                //开始加载
                notifyStateChange(LOADING);
                startScroll(0, -getScrollY() + mLoadMoreHeight);
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }

            } else if (mCurrentState == LOADING) {
                //继续回到加载的位置
                startScroll(0, -getScrollY() + mLoadMoreHeight);
            }
        } else {
            //这里不要设置恢复状态，因为当前若是在刷新或者正在加载的话，当滑动到刚好 getScrollY == 0 的时候
            //那么状态就会被重置为 NONE
            //reset();
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("zeal", "onNestedFling");
        return true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (!mIsSupportRefresh || !mIsSupportLoadMore) {
            return false;
        }
        if (getScrollY() == 0) {
            return false;
        }
        //fling(velocityY);
        return true;
    }

//    private void fling(float velocityY) {
//        mScroller.fling(0, getScrollY(), 0, (int) velocityY, 0, 0, 0, mRefreshHeight);
//        invalidate();
//    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {


        if (!mIsSupportRefresh || !mIsSupportLoadMore) {
            return;
        }


        if (dyUnconsumed != 0) {//还没滑动过，要在这里开始首次的滑动
            if (dyUnconsumed < 0) {//手指向下滑动

                scroll(dyUnconsumed);

            } else if (dyUnconsumed > 0) {//手指向上滑动

                scroll(dyUnconsumed);

            }
        }


    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {


        if (!mIsSupportRefresh || !mIsSupportLoadMore) {
            return;
        }

        if (getScrollY() == 0) {//表示当前初次滑动， smrl 就不响应这个事件，在 onNestedScroll 中去处理 smrl 的首次滑动。
            //询问父亲是否要响应这个事件
            int[] preConsume = new int[2];

            dispatchNestedPreScroll(dx, dy, preConsume, null);

            consumed[1] = preConsume[1];

            return;
        }


        //只有头部可见时才询问父亲是否要响应当前事件
//        if (getScrollY() < 0) {
////            当前是头部或者底部可见时，先询问父控件是否要滑动；
//            int[] preConsume = new int[2];
//            dispatchNestedPreScroll(dx, dy, preConsume, null);
//
//            int preConsumeY = preConsume[1];
//            dy = dy - preConsumeY;
//
//            if (dy == 0) {
//                //事件全部被父亲都消费了
//                consumed[1] = preConsumeY;
//                return;
//            }
//        }

        //判断rv是否能往上滑动
        boolean canScrollUpVertically = ViewCompat.canScrollVertically(mScrollView, 1);
        //判断rv是否能往下滑动
        boolean canScrollDownVertically = ViewCompat.canScrollVertically(mScrollView, -1);


        //正在刷新和正在加载的过程处理
        if (mCurrentState == REFRESHING) {

            if (dy < 0) {//手指向下滑动

                if (!canScrollDownVertically) {
                    consumed[1] = dy;
                    //Log.e("zeal", "手指向下滑动:" + dy);
                    scroll(dy, true);
                }

            } else if (dy > 0) {//手指向上滑动
                //不做任何处理

                if (Math.abs(getScrollY()) < dy) {
                    if (canScrollUpVertically) {//rv是否能向上滑动
                        //将剩余事件交给 rv 去处理
                        consumed[1] = -getScrollY();
                        scroll(-getScrollY());

                    } /**else {//事件交给 smrl 处理，显示底部

                     consumed[1] = dy;
                     scroll(dy);
                     }*/
                } else {//事件交给 smrl 处理，显示底部||只消耗dy大小，刚好回到原点
                    consumed[1] = dy;
                    scroll(dy);
                }
            }
            return;
        } else if (mCurrentState == LOADING) {


            if (dy > 0) {//手指向上海东
                if (!canScrollUpVertically) {
                    consumed[1] = dy;
                    scroll(dy, true);
                }
            } else if (dy < 0) {//手指向下滑动
                //判断当前划出的部分和即将滑动的事件作比较
                if (getScrollY() < Math.abs(dy)) {//当前需要滑动的事件*阻尼因子之后比当前加载底部可见部分还要多
                    //int deltaY = dy + getScrollY();

                    if (canScrollDownVertically) {//rv是否能向下滑动
                        //将剩余事件交给 rv 去处理
                        consumed[1] = -getScrollY();

                        scroll(-getScrollY());
                    }/** else {//事件交给 smrl 处理，显示底部

                     consumed[1] = dy;
                     scroll(dy);

                     }*/

                } else {//事件交给 smrl 处理，显示底部||只消耗dy大小，刚好回到原点
                    consumed[1] = dy;
                    scroll(dy);
                }
            }
            return;
        }


        if (getScrollY() < 0) {//刷新头部是可见的

            if (dy < 0) {//手指向下滑动

                consumed[1] = dy;

                //Log.e("zeal", "getScrollY = " + getScrollY() + "---dy:" + dy);
                scroll(dy, true);

            } else if (dy > 0) {//手指向上滑动

                //判断当前划出的部分和即将滑动的事件作比较
                if (Math.abs(getScrollY()) < dy) {//当前需要滑动的事件*阻尼因子之后比当前刷新头部可见部分还要多
                    //int deltaY = dy + getScrollY();

                    if (canScrollUpVertically) {//rv是否能向上滑动
                        //将剩余事件交给 rv 去处理
                        consumed[1] = -getScrollY();
                        scroll(-getScrollY());

                        //Log.e("zeal", "dy:" + (-getScrollY()));
                    } else {//事件交给 smrl 处理，显示底部

                        consumed[1] = dy;
                        scroll(dy);
                        //Log.e("zeal", "dy:" + dy);
                    }

                } else {//当前需要滑动的事件*阻尼因子之后比当前刷新头部可见部分还要小
                    consumed[1] = dy;
                    scroll(dy);
                    //Log.e("zeal", "dy:" + dy);
                }


            } else {//不处理任何事件，应该不会有这种状况发生

            }
        } else if (getScrollY() > 0) {//加载底部是可见的

            if (dy > 0) {//手指向上滑动

                consumed[1] = dy;

                scroll(dy, true);
            } else if (dy < 0) {//手指向下滑动

                //判断当前划出的部分和即将滑动的事件作比较
                if (getScrollY() < Math.abs(dy)) {//当前需要滑动的事件*阻尼因子之后比当前加载底部可见部分还要多
                    //int deltaY = dy + getScrollY();

                    if (canScrollDownVertically) {//rv是否能向下滑动
                        //将剩余事件交给 rv 去处理
                        consumed[1] = -getScrollY();
                        scroll(-getScrollY());
                    } else {//事件交给 smrl 处理，显示底部

                        consumed[1] = dy;
                        scroll(dy);

                    }

                } else {//当前需要滑动的事件*阻尼因子之后比当前刷新头部可见部分还要小
                    consumed[1] = dy;
                    scroll(dy);
                }


            } else {//不处理任何事件，应该不会有这种状况发生

            }
        }

    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }

    @Override
    public SmartFreshLayout2.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SmartFreshLayout2.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }


    /**
     * 通知外界修改状态
     */
    private void notifyStateChange(int state) {
        mCurrentState = state;
        if (mIsSupportRefresh && getScrollY() < 0) {
            if (mCurrentState == DRAG_DOWN) {
                mHeader.onStateChange(DRAG_DOWN, Math.abs(getScrollY() * 1.0f / mRefreshHeight));
            } else if (mCurrentState == RELEASE_REFRESH) {
                mHeader.onStateChange(RELEASE_REFRESH, Math.abs(getScrollY() * 1.0f / mRefreshHeight));
            } else if (mCurrentState == REFRESHING) {
                mHeader.onStateChange(REFRESHING, Math.abs(getScrollY() * 1.0f / mRefreshHeight));
            } else if (mCurrentState == REFRESHING_DONE) {
                mHeader.onStateChange(REFRESHING_DONE, Math.abs(getScrollY() * 1.0f / mRefreshHeight));
            } else if (mCurrentState == NONE) {
                mHeader.onStateChange(NONE, Math.abs(getScrollY() * 1.0f / mRefreshHeight));
            }
        } else if (mIsSupportLoadMore && getScrollY() > 0) {//支持加载更多
            if (mCurrentState == DRAG_UP) {
                mFooter.onStateChange(DRAG_UP, Math.abs(getScrollY() * 1.0f / mLoadMoreHeight));
            } else if (mCurrentState == RELEASE_LOADING) {
                mFooter.onStateChange(RELEASE_LOADING, Math.abs(getScrollY() * 1.0f / mLoadMoreHeight));
            } else if (mCurrentState == LOADING) {
                mFooter.onStateChange(LOADING, Math.abs(getScrollY() * 1.0f / mLoadMoreHeight));
            } else if (mCurrentState == LOADING_DONE) {
                mFooter.onStateChange(REFRESHING_DONE, Math.abs(getScrollY() * 1.0f / mLoadMoreHeight));
            } else if (mCurrentState == NONE) {
                mFooter.onStateChange(NONE, Math.abs(getScrollY() * 1.0f / mLoadMoreHeight));
            }
        }
    }

    /**
     * 开始滑动
     *
     * @param dx
     * @param dy
     */
    private void startScroll(int dx, int dy) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 1500);
        invalidate();
    }

    private void startScroll(int dx, int dy, int duration) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
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
        notifyStateChange(NONE);
        startScroll(0, -getScrollY());
    }


    /**
     * 滑动到指定的位置（绝对位置）
     *
     * @param y
     * @param isDump 是否需要阻尼效果
     */
    private void scroll(int y, boolean isDump) {
        if (getScrollY() == 0 && mCurrentState != REFRESHING && mCurrentState != LOADING) {
            notifyStateChange(DRAG_DOWN);
        } else if (getScrollY() < 0 && mCurrentState != REFRESHING) {
            if (Math.abs(getScrollY()) >= mRefreshHeight) {
                notifyStateChange(RELEASE_REFRESH);
            } else {
                notifyStateChange(DRAG_DOWN);
            }
        } else if (getScrollY() > 0 && mCurrentState != LOADING) {
            if (getScrollY() > Math.abs(mLoadMoreHeight)) {
                notifyStateChange(RELEASE_LOADING);
            } else {
                notifyStateChange(DRAG_UP);
            }
        }
        if (isDump) {
            if (Math.abs(y) > 50) {
                Log.e("zeal", "before y：" + y);
                y *= DUMP * DUMP * DUMP;
                Log.e("zeal", "after y：" + y);
            } else {
                y *= DUMP;
            }
        }
        scrollTo(0, getScrollY() + y);
    }


    /**
     * 滑动到指定的位置（绝对位置）
     *
     * @param y
     */
    private void scroll(int y) {
        scroll(y, false);
    }

    public interface RefreshListener {
        void onRefresh();
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

    public void setmOnRefreshListener(RefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    private boolean isRecovery;

    public void setRefresh(boolean fresh) {
        if (!fresh) {
            notifyStateChange(REFRESHING_DONE);
            isRecovery = true;
            startScroll(0, -getScrollY(), 300);
            reset();
            //只有在 300ms 之后才能重新滑动。
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRecovery = false;
                }
            }, 300);

        } else {
            notifyStateChange(REFRESHING);
            startScroll(0, -mRefreshHeight);
        }
    }

    public void setLoadMoreEnd() {
        notifyStateChange(REFRESHING_DONE);
        isRecovery = true;
        startScroll(0, -getScrollY(), 300);
        reset();
        //只有在 300ms 之后才能重新滑动。
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isRecovery = false;
            }
        }, 300);
    }

    private int mCurrentPointId = -100;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isRecovery) {
            return true;
        }
        //解决多指滑动问题
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            mCurrentPointId = ev.getPointerId(ev.getActionIndex());
        } else {

            if (mCurrentPointId != ev.getPointerId(ev.getActionIndex())) {
                if (action == MotionEventCompat.ACTION_POINTER_DOWN) {
                    return true;
                }
            }
        }
        //滑动结束后，只有在恢复到原始状态才能去发起第二次滑动
        //
        return super.dispatchTouchEvent(ev);
    }


    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }


    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
