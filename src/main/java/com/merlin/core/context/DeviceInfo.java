package com.merlin.core.context;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.merlin.core.network.NetWorkType;

import java.util.UUID;

/**
 * @author merlin
 */

public class DeviceInfo {

    public static DeviceInfo inst() {
        return InstHolder.info;
    }

    private static class InstHolder {
        private static final DeviceInfo info = new DeviceInfo();
    }

    private DeviceInfo() {
        this.sdkVersion = Build.VERSION.SDK_INT;
        this.osVersion = Build.VERSION.RELEASE;
        this.brand = Build.BRAND;
        this.model = Build.MODEL;
        this.phoneName = Build.MANUFACTURER;

        // AppVersionName & appVersionCode
        try {
            PackageInfo pi = MContext.app().getPackageManager().getPackageInfo(MContext.app().getPackageName(), 0);
            if (!TextUtils.isEmpty(pi.versionName)) {
                appVersion = pi.versionName;
            }
            if (pi.versionCode > 0) {
                appVersionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        readPhoneState();

        DisplayMetrics dm = MContext.app().getResources().getDisplayMetrics();
        this.scale = dm.density;
        // this.scale = context.getResources().getDisplayMetrics().density;
        this.widthPixels = dm.widthPixels;
        this.heightPixels = dm.heightPixels;
        this.xdpi = dm.xdpi;
        this.ydpi = dm.ydpi;
        this.widthDips = (int) (this.widthPixels / this.scale + 0.5f);
        this.heightDips = (int) (this.heightPixels / this.scale + 0.5f);

        this.androidId = "" + android.provider.Settings.Secure.getString(MContext.app().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        this.uuid = new UUID(androidId.hashCode(),
                (imei != null ? ((long) imei.hashCode() << 32) : "".hashCode()) | (simSerialNumber != null ? simSerialNumber : "").hashCode()).toString();

    }

    /**
     * SDK版本android SDK version
     */
    public final int sdkVersion;

    /**
     * 系统版本 android SDK version string
     */
    public final String osVersion;

    /**
     * APPVersion
     **/
    public String appVersion;

    /**
     * APPVersion
     **/
    public int appVersionCode;

    /**
     * 手机品牌
     */
    public final String brand;

    /**
     * 手机型号
     */
    public final String model;

    /**
     * mac地址
     */
    public String mac = "";

    /**
     * imei号，即DeviceId
     */
    public String imei;

    /**
     * imsi号: GSM手机的 IMEI 和 CDMA手机的 MEID.
     */
    public String imsi;

    /**
     * 手机号(基本上得不到)
     */
    public String cellphoneNum;

    public final float scale;

    /**
     * 屏幕高度(px)
     */
    public final int heightPixels;

    /**
     * 屏幕宽度(px)
     */
    public final int widthPixels;

    public final int heightDips;

    public final int widthDips;

    public final float xdpi;

    public final float ydpi;

    /**
     * 手机信号类型,如:
     * PHONE_TYPE_NONE  无信号
     * PHONE_TYPE_GSM   GSM信号
     * PHONE_TYPE_CDMA  CDMA信号
     */
    public int phoneType;
    /**
     * 服务商名称
     * 例如：中国移动、联通
     */
    public String simOperatorName;
    /**
     * SIM卡的序列号
     * 需要权限：READ_PHONE_STATE
     */
    public String simSerialNumber;
    /**
     * 唯一的用户ID：
     * 例如：IMSI(国际移动用户识别码) for a GSM phone.
     * 需要权限：READ_PHONE_STATE
     */
    public String subscriberId;
    /**
     * androidId
     */
    public String androidId;
    /**
     * UUID
     */
    public String uuid;
    /**
     * 手机名称
     */
    public String phoneName;

    //**********手机状态**************

    private void readPhoneState() {
        TelephonyManager tm = (TelephonyManager) MContext.app().getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(MContext.app(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_DENIED) {
            this.imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(tm.getSubscriberId())) {
                this.imsi = tm.getSubscriberId();
            } else {
                this.imsi = "";
            }
            if (!TextUtils.isEmpty(tm.getLine1Number())) {
                this.cellphoneNum = tm.getLine1Number();
            } else {
                this.cellphoneNum = "";
            }
            this.phoneType = tm.getPhoneType();
            this.simOperatorName = tm.getSimOperatorName();
            this.simSerialNumber = tm.getSimSerialNumber();
            this.subscriberId = tm.getSubscriberId();
        }
    }

    //**********网络类型**************

    public NetWorkType getNetWorkType() {
        Context context = MContext.app();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        if (activeInfo == null) {
            return NetWorkType.NO;
        } else {
            if (isConnected(context, ConnectivityManager.TYPE_WIFI)) {
                return NetWorkType.WIFI;
            } else if (isConnected(context, ConnectivityManager.TYPE_MOBILE)) {
                return getMobileType(tm.getNetworkType());
            }
        }
        return NetWorkType.UNKNOWN;
    }

    private boolean isConnected(@NonNull Context context, int type) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(type);
            return networkInfo != null && networkInfo.isConnected();
        } else {
            return isConnected(connMgr, type);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isConnected(@NonNull ConnectivityManager connMgr, int type) {
        Network[] networks = connMgr.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network mNetwork : networks) {
            networkInfo = connMgr.getNetworkInfo(mNetwork);
            if (networkInfo != null && networkInfo.getType() == type) {
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    private NetWorkType getMobileType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:  // ~ 25 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:  // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:  // ~ 100 kbps
                return NetWorkType.G2;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:  // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:  // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:  // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:  // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:  // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:  // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:  // ~ 10-20 Mbps
                return NetWorkType.G3;
            case TelephonyManager.NETWORK_TYPE_LTE:  // ~ 10+ mbps
                return NetWorkType.G4;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return NetWorkType.UNKNOWN;
            default:
                return NetWorkType.UNKNOWN;
        }
    }

}
