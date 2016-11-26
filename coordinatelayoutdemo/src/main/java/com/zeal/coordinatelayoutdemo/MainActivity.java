package com.zeal.coordinatelayoutdemo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * activity_main:实现了只有手指往下滑动，标题栏就可以见
 * CoordinateLayout作为跟布局
 * 在布局中指定标题栏显示的Behavior行为-->app:layout_scrollFlags="scroll|enterAlways"
 * <p>
 * 标记：
 * scroll：若是当前view需要滑动，那么就必须要这个标记,没有设置这个标记将被固定在屏幕底部
 * enterAlways：当滑动组件向下滚动时，标题栏会直接往下滚动。
 * enterAlwaysCollapsed：当你的视图已经设置minHeight属性又使用此标志时,同时也要使用上enterAlways属性
 * ，你的视图只能已最小高度进入，只有当滚动视图到达顶部时才扩 大到完整高度。
 * exitUntilCollapsed：当标题栏要往上逐渐“消逝”时，会一直往上滑动，直到剩下的的高度达到它的最小高度，
 * 再响应滑动组件的内部滑动事件。
 * <p>
 * 因为CoordinateLayout是FrameLayout，控制NestedScrollView在AppbarLayout的下面：
 * 设置标记app:layout_behavior="@string/appbar_scrolling_view_behavior"
 * 这个属性可以让NestedScrollView显示在AppBarLayout的下面
 * <p>
 * toolbar的使用：
 * Theme.AppCompat.Light.NoActionBar
 * Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 * setSupportActionBar(toolbar);
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_colleaselayout);

        //当系统版本大于等于4.4时，将状态栏设置为透明状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //显示一个返回键操作按钮
        //ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.a1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "NavigationOnClickListenern", Toast.LENGTH_SHORT).show();
            }
        });
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置内容在Navigation展示时的距离，包括它的宽度计算在内
        //toolbar.setContentInsetStartWithNavigation(220);

        //toolbar.setTitle(null);//这种方式是不能讲title隐藏掉的
        //getSupportActionBar().setTitle(null);//这种方式可以实现
        //toolbar.setLogo(R.mipmap.ic_launcher);
        //toolbar.setSubtitle("副标题");
        //toolbar.setSubtitleTextColor(Color.parseColor("#ffffff"));


        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout);
        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#ffffff"));
        //设置折叠后状态栏的颜色为透明
        collapsingToolbarLayout.setStatusBarScrimColor(Color.TRANSPARENT);
    }
}
