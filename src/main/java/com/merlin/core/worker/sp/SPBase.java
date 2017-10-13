package com.merlin.core.worker.sp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.merlin.core.context.MContext;

/**
 * zal
 */
public class SPBase {

    private SharedPreferences dataSP = null;

    protected SPBase(String name) {
        this.dataSP = MContext.inst().app().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void setData(String key, Object value) {
        setValue(dataSP, key, value);
    }

    /**
     * @param key
     * @return default ""
     */
    public String getDataString(String key) {
        return getDataString(key, "");
    }

    public String getDataString(String key, String defValue) {
        if (dataSP != null) {
            return dataSP.getString(key, defValue);
        }
        return defValue;
    }

    /**
     * @param key
     * @return default false
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (dataSP != null) {
            return dataSP.getBoolean(key, defValue);
        }
        return defValue;
    }

    /**
     * @param key
     * @return default 0
     */
    public long getLong(String key) {
        return getLong(key, 0l);
    }

    public long getLong(String key, long defValue) {
        if (dataSP != null) {
            return dataSP.getLong(key, defValue);
        }
        return defValue;
    }

    /**
     * @param key
     * @return default 0
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        if (dataSP != null) {
            return dataSP.getInt(key, defValue);
        }
        return defValue;
    }

    /**
     * @param key
     * @return default 0
     */
    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(String key, float defValue) {
        if (dataSP != null) {
            return dataSP.getFloat(key, defValue);
        }
        return defValue;
    }

    @SuppressLint("NewApi")
    private void setValue(SharedPreferences sp, String key, Object value) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            if (value instanceof String) {
                editor.putString(key, String.valueOf(value));
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, Boolean.valueOf(String.valueOf(value)));
            } else if (value instanceof Long) {
                editor.putLong(key, Long.valueOf(String.valueOf(value)));
            } else if (value instanceof Integer) {
                editor.putInt(key, Integer.valueOf(String.valueOf(value)));
            } else if (value instanceof Float) {
                editor.putFloat(key, Float.valueOf(String.valueOf(value)));
            }

            commitOrApply(editor);
        } else {
        }
    }

    /**
     * @param keys
     * @return
     */
    public boolean remove(String... keys) {
        if (dataSP == null) {
            return false;
        }

        if (keys == null || keys.length <= 0) {
            return false;
        }

        SharedPreferences.Editor editor = dataSP.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        commitOrApply(editor);
        return true;
    }

    private void commitOrApply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    /**
     * 清除
     */
    public void clear() {
        if (dataSP != null) {
            commitOrApply(dataSP.edit().clear());
        }
    }

}
