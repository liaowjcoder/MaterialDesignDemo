package storage.zeal.com.smartrefreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import storage.zeal.com.smartrefreshlayout.Header;
import storage.zeal.com.smartrefreshlayout.view.SmartFreshLayout;

/**
 * Created by liaowj on 2017/5/12.
 */

public class MyHeaderView extends android.support.v7.widget.AppCompatTextView implements Header {
    public MyHeaderView(Context context) {
        super(context);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onStateChange(int state,float y) {
        switch (state) {
            case SmartFreshLayout.DRAG_DOWN:
                setText("下拉刷新");
                break;
            case SmartFreshLayout.RELEASE_REFRESH:
                setText("松开刷新");
                break;
            case SmartFreshLayout.REFRESHING:
                setText("正在刷新");
                break;
            case SmartFreshLayout.REFRESHING_DONE:
                setText("刷新完毕");
                break;
        }
    }


    @Override
    public View getHeaderView() {
        return this;
    }

    @Override
    public int getRefreshHeight() {
        return dip2Px(100);
    }

    public int dip2Px(int dip) {
        float density = getResources().getDisplayMetrics().density;

        int px = (int) (density * dip + .5f);
        return px;
    }

}
