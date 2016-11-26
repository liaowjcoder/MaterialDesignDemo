package com.zeal.coordinatelayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * activity_main:实现了只有手指往下滑动，标题栏就可以见
 * CoordinateLayout作为跟布局
 * 在布局中指定标题栏显示的Behavior行为-->app:layout_scrollFlags="scroll|enterAlways"
 *
 * 标记：
 * scroll：若是当前view需要滑动，那么就必须要这个标记,没有设置这个标记将被固定在屏幕底部
 * enterAlways：当滑动组件向下滚动时，标题栏会直接往下滚动。
 * enterAlwaysCollapsed：当你的视图已经设置minHeight属性又使用此标志时,同时也要使用上enterAlways属性
 * ，你的视图只能已最小高度进入，只有当滚动视图到达顶部时才扩 大到完整高度。
 * exitUntilCollapsed：当标题栏要往上逐渐“消逝”时，会一直往上滑动，直到剩下的的高度达到它的最小高度，
 * 再响应滑动组件的内部滑动事件。
 *
 * 因为CoordinateLayout是FrameLayout，控制NestedScrollView在AppbarLayout的下面：
 * 设置标记app:layout_behavior="@string/appbar_scrolling_view_behavior"
 * 这个属性可以让NestedScrollView显示在AppBarLayout的下面
 *
 * toolbar的使用：
 * Theme.AppCompat.Light.NoActionBar
 * Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 * setSupportActionBar(toolbar);
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
}
