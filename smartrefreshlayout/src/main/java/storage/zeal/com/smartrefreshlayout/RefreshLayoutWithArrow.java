package storage.zeal.com.smartrefreshlayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import storage.zeal.com.smartrefreshlayout.view.SmartFreshLayout;

/**
 * Created by liaowj on 2017/5/12.
 */

public class RefreshLayoutWithArrow extends LinearLayout implements Header {
    private ImageView mImageView;

    private ScaleAnimation mScaleAnimation;
    private RotateAnimation mRotateAnimation;

    public RefreshLayoutWithArrow(Context context) {
        super(context);
    }

    public RefreshLayoutWithArrow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.refresh_layout, this);

        mImageView = (ImageView) findViewById(R.id.imageview);


    }

    @Override
    public void onStateChange(int state, float per) {
        switch (state) {
            case SmartFreshLayout.DRAG_DOWN:
                scale(per);
                break;
            case SmartFreshLayout.RELEASE_REFRESH:
                scale(per);
                break;
            case SmartFreshLayout.REFRESHING:
                rotate();
                break;
            case SmartFreshLayout.REFRESHING_DONE:
                mImageView.clearAnimation();
                mRotateAnimation.cancel();
                mPer = 0;
                break;
        }
    }


    @Override
    public View getHeaderView() {
        return this;
    }

    @Override
    public int getRefreshHeight() {
        return dip2Px(50);
    }

    public int dip2Px(int dip) {
        float density = getResources().getDisplayMetrics().density;

        int px = (int) (density * dip + .5f);
        return px;
    }

    private float mPer;

    private void scale(float per) {
        mImageView.clearAnimation();
        mScaleAnimation = new ScaleAnimation(mPer, per, mPer, per, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setFillAfter(true);
        mScaleAnimation.setDuration(200);
        mImageView.startAnimation(mScaleAnimation);

        mPer = per;

//        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
//        layoutParams.width = (int) (layoutParams.width*per);
//        layoutParams.height = (int) (layoutParams.height*per);
//        mImageView.setLayoutParams(layoutParams);

    }

    private void rotate() {
        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setRepeatCount(200);
        mRotateAnimation.setDuration(500);
        mRotateAnimation.setRepeatMode(Animation.INFINITE);
        mImageView.clearAnimation();
        mImageView.startAnimation(mRotateAnimation);
    }


}
