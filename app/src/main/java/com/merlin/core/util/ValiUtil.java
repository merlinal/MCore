package com.merlin.core.util;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by ncm on 16/11/13.
 */

public class ValiUtil {

    /**
     * 字符串判空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 字符串判空&字符串不能只是空格
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return TextUtils.isEmpty(str) || str.trim().length() <= 0;
    }

    /**
     * 集合判空
     *
     * @param coll
     * @return
     */
    public static boolean isEmpty(Collection<?> coll) {
        return ((coll == null) || (coll.isEmpty()));
    }

    /**
     * map判空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return ((map == null) || (map.isEmpty()));
    }

    /**
     *
     * @param o
     * @return
     */
    public static boolean isNull(Object o){
        return o == null;
    }

}
