package com.merlin.core.context;

import android.app.Application;

import com.merlin.core.util.LogUtil;

/**
 * Created by ncm on 16/11/7.
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化共享资源
        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.e("onTerminate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.e("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.e("onTrimMemory level = " + level);
    }

    private void init() {
        AppContext.inst().setApp(this);
    }

}
