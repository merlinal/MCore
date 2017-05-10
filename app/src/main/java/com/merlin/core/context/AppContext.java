package com.merlin.core.context;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.merlin.core.BuildConfig;
import com.merlin.core.network.NetWorkListener;
import com.merlin.core.network.NetWorkType;

import java.util.ArrayList;

/**
 * Created by ncm on 16/10/31.
 */

public class AppContext {

    /**
     * 单例
     */
    private AppContext() {
    }

    public static AppContext inst() {
        return InstHolder.appContext;
    }

    private static class InstHolder {
        private final static AppContext appContext = new AppContext();
    }

    private Application application;
    private Activity currentActivity;
    private NetWorkType netWorkType;
    private ArrayList<NetWorkListener> netWorkListeners = new ArrayList<>();

    /**
     * Application
     *
     * @return
     */
    public Application app() {
        return application;
    }

    public void setApp(Application application) {
        this.application = application;
    }

    /**
     * 当前Activity
     *
     * @return
     */
    public Activity activity() {
        return currentActivity;
    }

    public void setActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * 是否debug模式
     *
     * @return
     */
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    /**
     * app名称
     *
     * @return
     */
    public String getAppName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = app().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(app().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public DeviceInfo deviceInfo() {
        return DeviceInfo.inst();
    }

    /**
     * 监听网络变化
     */
    public void notifyNetWorkTypeChanged() {
        NetWorkType temp = DeviceInfo.inst().getNetWorkType();
        if (netWorkType != temp) {
            netWorkType = temp;
            if (netWorkListeners != null && netWorkListeners.size() > 0) {
                for (NetWorkListener listener : netWorkListeners) {
                    if (listener != null) {
                        listener.onNetWorkChanged(netWorkType);
                    }
                }
            }
        }
    }

    public void removeNetWorkListener(NetWorkListener netWorkListener) {
        netWorkListeners.remove(netWorkListener);
    }

    public void addNetWorkListener(NetWorkListener netWorkListener) {
        netWorkListeners.add(netWorkListener);
    }

}
