package com.zeal.bottomsheetdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * Created by zeal on 16/11/24.
 */

public class NestedScrollViewDemo extends AppCompatActivity {
    private static final String TAG = "zeal";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nestedscrollview);

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedscrollview);
        final BottomSheetBehavior<NestedScrollView> sheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.e(TAG, "onStateChanged: newState:" + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e(TAG, "onStateChanged: slideOffset:" + slideOffset);
            }

        });


        TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //4 STATE_COLLAPSED
                //1 STATE_DRAGGING
                //2 STATE_SETTLING
                //3 STATE_EXPANDED   0->slideOffset:1.0

                //3 2 4 slideOffest 1-0
                Log.e(TAG, "onClick: heetBehavior.getState():" + sheetBehavior.getState());
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED&&sheetBehavior.getState()!=BottomSheetBehavior.STATE_DRAGGING) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED&&sheetBehavior.getState()!=BottomSheetBehavior.STATE_DRAGGING) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }

        });
    }
}
