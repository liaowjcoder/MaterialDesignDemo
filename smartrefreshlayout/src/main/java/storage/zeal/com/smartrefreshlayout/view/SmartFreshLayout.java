package storage.zeal.com.smartrefreshlayout.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import storage.zeal.com.smartrefreshlayout.Footer;
import storage.zeal.com.smartrefreshlayout.Header;

/**
 * Created by liaowj on 2017/5/11.
 */

public class SmartFreshLayout extends ViewGroup implements NestedScrollingParent {

    private final NestedScrollingParentHelper parentHelper;

    private RecyclerView mScrollView;


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
    private static final float DUMP2 = 0.4f;

    private boolean mIsSupportRefresh;
    private boolean mIsSupportLoadMore;

    private int mRefreshHeight;
    private int mLoadMoreHeight;

    private Header mHeader;
    private Footer mFooter;

    private RefreshListener mOnRefreshListener;
    private LoadMoreListener mOnLoadMoreListener;


    public SmartFreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.CYAN);
        parentHelper = new NestedScrollingParentHelper(this);
        init();
    }

    private void init() {
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
            if (childView instanceof Header) {
                mHeader = (Header) childView;
            } else if (childView instanceof RecyclerView) {
                mScrollView = (RecyclerView) childView;
            } else if (childView instanceof Footer) {
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
                    getMeasuredWidth(), mScrollView.getMeasuredHeight() + mFooter.getLoadMoreView().getMeasuredHeight());

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
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (!mIsSupportRefresh || !mIsSupportLoadMore) {
            return false;
        }
        if (getScrollY() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if (!mIsSupportRefresh || !mIsSupportLoadMore) {
            return;
        }

        //判断rv是否能往下滑动
        //positive to check scrolling down
        boolean canScrollDownVertically = ViewCompat.canScrollVertically(mScrollView, -1);
        //判断rv是否能往上滑动
        boolean canScrollUpVertically = ViewCompat.canScrollVertically(mScrollView, 1);

        if (mCurrentState == REFRESHING) {//正在刷新的状态
            if (dy < 0 && !canScrollDownVertically) {
                consumed[1] = dy;
                dy *= DUMP2;
                scroll(getScrollY() + dy);
                return;
            }
        } else if (mCurrentState == LOADING) {//正在加载的状态
            if (dy > 0 && !canScrollUpVertically) {
                consumed[1] = dy;
                dy *= DUMP2;
                scroll(getScrollY() + dy);
                return;
            }
        }

        if (dy != 0) {
            if (dy < 0) {//手指往下滑动
                if (!canScrollDownVertically && getScrollY() < 0) {//不能往下滑动，该响应父控件的滑动了
                    consumed[1] = dy;
                    //开始滑动父控件,下拉需要阻尼效果
                    scroll(getScrollY() + dy, true);
                } else if (!canScrollUpVertically && getScrollY() > 0) {//不能往上滑动，该响应父控件的滑动了
                    consumed[1] = dy;
                    //开始滑动父控件,下拉需要阻尼效果
                    scroll(getScrollY() + dy, true);
                } else {
                    if (!canScrollDownVertically) {//不能往下滑动
                        consumed[1] = dy;
                        scroll(getScrollY() + dy);
                    }
                }
            } else {//手指向上滑动
                if (getScrollY() < 0 && !canScrollDownVertically) {//刷新头可见,并且不可以向下下拉
                    consumed[1] = dy;
                    scroll(getScrollY() + dy);
                } else if (getScrollY() > 0 && !canScrollUpVertically) {//加载底部显示,并且不可以向上上拉
                    //不能向上滑动
                    consumed[1] = dy;
                    scroll(getScrollY() + dy);
                } else {
                    if (!canScrollUpVertically
                            && mCurrentState > REFRESHING//
                            && mCurrentState < REFRESHING_DONE) {//不能往上滑动
                        consumed[1] = dy;
                        scroll(getScrollY() + dy);
                    }
                }
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
        //判断当前 y 值是处于哪种状态的
        if (y < 0 && mCurrentState != REFRESHING) {
            if (Math.abs(getScrollY()) >= mRefreshHeight) {//表示大于刷新头部了
                //松开刷新
                notifyStateChange(RELEASE_REFRESH);
            } else {
                //下拉刷新
                notifyStateChange(DRAG_DOWN);
            }
        } else if (y > 0 && mCurrentState != LOADING) {//手指向上滑动&&不处于加载状态
            if (Math.abs(getScrollY()) >= mLoadMoreHeight) {//表示大于刷新头部了
                //松开加载
                notifyStateChange(RELEASE_LOADING);
            } else {
                //上拉加载
                notifyStateChange(DRAG_UP);
            }
        }

        if (isDump) {
            y *= DUMP;
        }
        //边界控制
//        if (y > 0) {
//            y = 0;
//        }
        scrollTo(0, y);

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

    public void setRefresh(boolean fresh) {
        if (!fresh) {
            notifyStateChange(REFRESHING_DONE);
            startScroll(0, -getScrollY());
            reset();
        } else {
            notifyStateChange(REFRESHING);
            startScroll(0, -mRefreshHeight);
        }
    }


}
