package storage.zeal.com.smartrefreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import storage.zeal.com.smartrefreshlayout.view.SmartFreshLayout;

/**
 * Created by liaowj on 2017/5/12.
 */

public class MyFooterView extends android.support.v7.widget.AppCompatTextView implements Footer {
    public MyFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFooterView(Context context) {
        super(context);
        setGravity(Gravity.CENTER|Gravity.BOTTOM);
    }

    @Override
    public void onStateChange(int state, float per) {
        switch (state) {
            case SmartFreshLayout.DRAG_UP:
                setText("上拉加载");
                break;
            case SmartFreshLayout.RELEASE_LOADING:
                setText("松开加载");
                break;
            case SmartFreshLayout.LOADING:
                setText("正在加载更多");
                break;
            case SmartFreshLayout.LOADING_DONE:
                setText("加载完毕");
                break;
        }
    }

    @Override
    public View getLoadMoreView() {
        return this;
    }


    public int getLoadHeight() {
        return dip2Px(50);
    }

    public int dip2Px(int dip) {
        float density = getResources().getDisplayMetrics().density;

        int px = (int) (density * dip + .5f);
        return px;
    }
}
