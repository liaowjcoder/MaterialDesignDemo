package storage.zeal.com.smartrefreshlayout;

import android.view.View;

/**
 * Created by liaowj on 2017/5/12.
 */

public interface Header {

    void onStateChange(int state,float per);

    View getHeaderView();

    int getRefreshHeight();
}
