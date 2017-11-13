package com.merlin.core.context;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.merlin.core.util.MGson;
import com.merlin.core.util.MLog;
import com.merlin.core.util.MUtil;
import com.merlin.core.util.MVerify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zal
 */
public class UriInfo {

    protected UriInfo() {
        init();
    }

    private String scheme = "merlin://";

    private Map<String, String> uriActivity = new HashMap<>();

    public void init() {
        loadUriMatcher(MContext.app().getPackageName(), MContext.app().getResources());
    }

    private void loadUriMatcher(String packageName, Resources res) {
        try {
            ApplicationInfo appInfo = MContext.app().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            MLog.i("初始化host");
            if (appInfo != null && appInfo.metaData != null) {
                Bundle metaDataBundle = appInfo.metaData;
                Set<String> set = metaDataBundle.keySet();
                if (set != null) {
                    for (String string : set) {
                        if ((string != null) && (string.startsWith("uri_"))) {
                            MLog.i(string + "===" + metaDataBundle.get(string));
                            int rawResId = metaDataBundle.getInt(string);
                            List<UriFile> uriList = null;
                            try {
                                StringBuffer sb = new StringBuffer();
                                String str;
                                BufferedReader br = new BufferedReader(getStreamReader(res.openRawResource(rawResId)));
                                while ((str = br.readLine()) != null) {
                                    sb.append(str);
                                }
                                MLog.i(sb.toString());
                                uriList = MGson.toList(sb.toString(), new TypeToken<List<UriFile>>() {
                                }.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (uriList != null) {
                                for (UriFile uri : uriList) {
                                    if (MVerify.isBlank(uri.scheme)) {
                                        uri.scheme = scheme;
                                    }
                                    uriActivity.put(uri.getKey(), uri.clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            MLog.wtf(e);
        } catch (Exception e) {
            MLog.wtf(e);
        }
    }

    public InputStreamReader getStreamReader(InputStream ins) {
        if (ins != null) {
            try {
                return new InputStreamReader(ins, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                MLog.wtf(e);
            }
        }
        return null;
    }

    public Intent getIntent(String url) {
        Intent it = null;
        String classpath = getClassPath(url);
        if (classpath != null) {
            Class clazz = MUtil.loadClass(classpath);
            if (clazz != null) {
                it = new Intent(MContext.app(), clazz);
                //参数
                it.putExtras(getParams(url));
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        return it;
    }

    private String getClassPath(String url) {
        //目标类
        String key = url;
        int index = url.indexOf("?param=");
        if (index > 0) {
            key = url.substring(0, index);
        }
        key = key.replace(scheme, "");
        return uriActivity.get(key);
    }

    private Bundle getParams(String url) {
        Bundle bundle = new Bundle();
        Uri uri = Uri.parse(url);
        String param = uri.getQueryParameter("param");
        if (!MVerify.isBlank(param)) {
            try {
                JSONObject jsonObject = new JSONObject(param);
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    bundle.putString(it.next(), jsonObject.getString(it.next()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*if (paramStr != null && paramStr.length() > 0) {
            String[] params = paramStr.split("&");
            for (String param : params) {
                String[] nameValues = param.split("=");
                if (nameValues.length > 1) {
                    bundle.putString(nameValues[0], nameValues[1]);
                }
            }
        }*/
        return bundle;
    }

}