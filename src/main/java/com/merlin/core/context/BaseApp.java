package com.merlin.core.context;

import android.app.Application;

import com.merlin.core.util.MLog;

/**
 * Created by zal on 17/11/7.
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //全局工具
        MContext.inst().setApp(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MLog.e("onTerminate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MLog.e("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        MLog.e("onTrimMemory level = " + level);
    }

}
