package com.zeal.coordinatelayoutdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TabLayoutActivity extends AppCompatActivity {

    private static final String TAG = "zeal";
    private TabLayout mTl;
    private ViewPager mVp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_tablayout);

        mTl = (TabLayout) findViewById(R.id.id_tablayout);
        mVp = (ViewPager) findViewById(R.id.id_vp);


        //添加两个tab
        //mTl.addTab(mTl.newTab().setText("登陆"));
        //mTl.addTab(mTl.newTab().setText("注册"));

        //让tablayout与viewpager建立关联关系
        mTl.setupWithViewPager(mVp);

        mVp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView textView = new TextView(container.getContext());
                textView.setText("position:" + position);
                container.addView(textView);
                return textView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if(position ==0) {
                    return "登陆";
                }else {
                    return "注册";
                }
            }
        });


    }
}

