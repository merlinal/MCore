package com.merlin.core.context;

import android.app.Activity;
import android.app.Application;

import com.merlin.core.network.NetWorkObservable;
import com.merlin.core.network.NetWorkObserver;

/**
 * Created by ncm on 17/9/24.
 */

public class MContext {

    /**
     * 单例
     */
    private MContext() {
    }

    public static MContext inst() {
        return InstHolder.appContext;
    }

    private static class InstHolder {
        private final static MContext appContext = new MContext();
    }

    private Application application;
    private Activity currentActivity;
    private boolean isDebug = true;

    private NetWorkObservable netWorkObservable;


    /**
     * Application
     *
     * @return
     */
//    public Application getApp() {
//        return application;
//    }

    public void setApp(Application application) {
        this.application = application;
    }

    /**
     * 当前Activity
     *
     * @return
     */
//    public Activity getActivity() {
//        return MUtil.isDestroyed(currentActivity) ? null : currentActivity;
//    }

    public void setActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * 是否debug模式
     *
     * @return
     */
//    public boolean isDebug() {
//        return isDebug;
//    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 监听网络变化
     */
    public void notifyNetWorkTypeChanged() {
        if (netWorkObservable != null) {
            netWorkObservable.notifyChanged();
        }
    }

    public void registerNetObserver(NetWorkObserver observer) {
        if (netWorkObservable == null) {
            netWorkObservable = new NetWorkObservable();
        }
        netWorkObservable.registerObserver(observer);
    }

    public void unregisterNetObserver(NetWorkObserver observer) {
        netWorkObservable.unregisterObserver(observer);
        if (netWorkObservable.getObserverSize() == 0) {
            netWorkObservable = null;
        }
    }

    public void unregisterNetObserverAll() {
        netWorkObservable.unregisterAll();
        netWorkObservable = null;
    }

    /**
     * 设备信息
     */
    public static DeviceInfo device() {
        return DeviceInfo.inst();
    }

    public static Application app() {
        return MContext.inst().application;
    }

    public static Activity activity() {
        return MContext.inst().currentActivity;
    }

    public static boolean isDebug() {
        return MContext.inst().isDebug;
    }

}
