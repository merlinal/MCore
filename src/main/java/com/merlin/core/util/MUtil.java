package com.merlin.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.merlin.core.context.MContext;

/**
 * Created by ncm on 16/11/3.
 */

public class MUtil {

    /**
     * get view from activity
     *
     * @param activity
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends View> T view(Activity activity, int id) {
        return (T) activity.getWindow().getDecorView().findViewById(id);
    }

    /**
     * get view from parentView
     *
     * @param view
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends View> T view(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * get view from resource
     *
     * @param layoutId
     * @param parent
     * @param isAttach
     * @return
     */
    public static View view(int layoutId, ViewGroup parent, boolean isAttach) {
        return inflater().inflate(layoutId, parent, isAttach);
    }

    /**
     * @return
     */
    public static LayoutInflater inflater() {
        return LayoutInflater.from(MContext.app());
    }

    /**
     * get string from resource
     *
     * @param stringId
     * @return
     */
    public static String string(int stringId) {
        return MContext.app().getString(stringId);
    }

    /**
     * get color from resource
     *
     * @param colorId
     * @return
     */
    public static int color(int colorId) {
        return ContextCompat.getColor(MContext.app(), colorId);
    }

    /**
     * get Drawable from resource
     *
     * @param drawableId
     * @return
     */
    public static Drawable drawable(int drawableId) {
        return ContextCompat.getDrawable(MContext.app(), drawableId);
    }

    /**
     * get dimen from resource
     *
     * @param dimenId
     * @return
     */
    public static float dimen(int dimenId) {
        return MContext.app().getResources().getDimension(dimenId);
    }

    /**
     * dp2px
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue) {
        final float scale = MContext.app().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     *
     * @param pxValue
     * @return
     */
    public static float px2dp(float pxValue) {
        final float scale = MContext.app().getResources().getDisplayMetrics().density;
        return (pxValue - 0.5f) / scale;
    }

    /**
     * 加载类实例
     *
     * @param classpath
     * @param <T>
     * @return
     */
    public static <T extends Object> T loadInstance(String classpath) {
        T t = null;
        if (!MVerify.isBlank(classpath)) {
            try {
                t = (T) MContext.app().getClassLoader().loadClass(classpath).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                return t;
            }
        }
        return t;
    }

    /**
     * 加载类
     *
     * @param classpath
     * @return
     */
    public static Class<?> loadClass(String classpath) {
        if (!MVerify.isBlank(classpath)) {
            try {
                return MContext.app().getClassLoader().loadClass(classpath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 触摸点是否在EditText内
     *
     * @param v     当前焦点view
     * @param event 触摸事件
     * @return
     */
    public static boolean isInEditText(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom;
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param v
     */
    public static void hideSoftInput(Activity context, View v) {
        if (v != null && v.getWindowToken() != null && context != null && isSoftShowing(context)) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     */
    public static void hideSoftInput(Activity context) {
        if (null != context.getCurrentFocus() && null != context.getCurrentFocus().getWindowToken()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }

    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(Activity context) {
        if (context != null && !isSoftShowing(context)) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 键盘是否已显示
     *
     * @param context
     * @return
     */
    public static boolean isSoftShowing(Activity context) {
        return context.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 获取主题颜色
     *
     * @return
     */
    public static int colorPrimary() {
        TypedValue typedValue = new TypedValue();
        MContext.app().getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        TypedArray array = MContext.app().obtainStyledAttributes(typedValue.resourceId, new int[]{android.R.attr.colorPrimary});
        int color = array.getColor(0, Color.TRANSPARENT);
        array.recycle();
        return color;
    }

    /**
     * Activity是否已销毁
     *
     * @param activity
     * @return
     */
    public static boolean isDestroyed(Activity activity) {
        if (activity == null) {
            return true;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        } else {
            return activity.isFinishing();
        }
    }

    /**
     * android.support.v4.app.Fragment 是否已销毁
     *
     * @param fragment
     * @return
     */
    public static boolean isDestroyed(android.support.v4.app.Fragment fragment) {
        if (isDestroyed(fragment.getActivity()) || fragment.isDetached()) {
            return true;
        }
        return false;
    }

    /**
     * /**
     * android.app.Fragment 是否已销毁
     *
     * @param fragment
     * @return
     */
    public static boolean isDestroyed(android.app.Fragment fragment) {
        if (isDestroyed(fragment.getActivity()) || fragment.isDetached()) {
            return true;
        }
        return false;
    }

}
