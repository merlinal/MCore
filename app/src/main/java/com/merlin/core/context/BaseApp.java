package com.merlin.core.context;

import android.app.Application;

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

    private void init() {
        AppContext.inst().setApp(this);
    }

}
