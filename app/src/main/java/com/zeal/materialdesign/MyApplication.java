package com.zeal.materialdesign;

import android.app.Application;

import com.antfortune.freeline.FreelineCore;

/**
 * Created by zeal on 2017/1/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FreelineCore.init(this);
    }
}
