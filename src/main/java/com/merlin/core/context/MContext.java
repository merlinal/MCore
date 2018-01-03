package com.merlin.core.context;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.merlin.core.network.NetWorkObservable;
import com.merlin.core.network.NetWorkObserver;
import com.merlin.core.util.MLog;

import java.lang.reflect.Method;

/**
 * Created by ncm on 17/9/24.
 *
 * @author zal
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

    private PackageInfo packageInfo;

    private UriInfo uriInfo;

    /**
     * Application
     *
     * @return
     */
    public void setApp(Application application) {
        this.application = application;
    }

    /**
     * 当前Activity
     *
     * @return
     */
    public void setActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * 是否debug模式
     *
     * @return
     */
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
     * packageInfo
     */
    private PackageInfo getPackageInfo() {
        if (packageInfo == null) {
            try {
                packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                MLog.wtf(e);
            }
        }
        return packageInfo;
    }


    /**
     * 获取当前进程名称，mate该方法耗时2ms，红米note 5ms
     *
     * @return
     */
    private String getProcessName() {
        try {
            Class obj_class = Class.forName("android.app.ActivityThread");
            Method method = obj_class.getMethod("currentActivityThread");
            method.setAccessible(true);
            Object object = method.invoke(null);
            method = obj_class.getMethod("getProcessName");
            method.setAccessible(true);
            return (String) method.invoke(object, new Object[0]);
        } catch (Exception localException) {
            MLog.wtf(localException);
        }
        return null;
    }

    /**
     * 获取App名称
     *
     * @return
     */
    public String getAppName() {
        return application.getResources().getString(application.getApplicationInfo().labelRes);
    }

    /**
     * 自定义UriInfo信息
     *
     * @return
     */
    private UriInfo getUriInfo() {
        return new UriInfo();
    }

    //*********************快捷入口*******************************

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

    public static String versionName() {
        return MContext.inst().getPackageInfo().versionName;
    }

    public static int versionCode() {
        return MContext.inst().getPackageInfo().versionCode;
    }

    public static String processName() {
        return MContext.inst().getProcessName();
    }

    public static String appName() {
        return MContext.inst().getAppName();
    }

    public static Intent getIntent(String url) {
        return MContext.inst().getUriInfo().getIntent(url, null, null);
    }

    public static Intent getIntent(String url, String password) {
        return MContext.inst().getUriInfo().getIntent(url, password, null);
    }

    public static Intent getIntent(String url, String password, String model) {
        return MContext.inst().getUriInfo().getIntent(url, password, model);
    }

}
