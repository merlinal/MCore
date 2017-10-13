package com.merlin.core.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Json操作
 *
 * @author zhaoanlin
 */
public class MGson {

    private static final String TAG = "MGson";
    private static Gson gson = new Gson();

    /**
     * 简单类型转换为json
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    /**
     * 转换为json
     *
     * @param o
     * @param dateFormat
     * @param version
     * @return
     */
    public static String toJson(Object o, String dateFormat, double version) {
        return getComplexGson(new String[]{dateFormat, version + ""}).toJson(o);
    }

    /**
     * Map转换为json
     *
     * @param map
     * @return
     */
    public static String mapToJson(Map<?, ?> map) {
        return getComplexGson().toJson(map);
    }

    /**
     * jsonToMap
     *
     * @param jsonString
     * @param type
     * @return
     */
    public static <T> Map<String, T> toMap(String jsonString, Type type) {
        Map<String, T> map = null;
        try {
            map = gson.fromJson(jsonString, type);
        } catch (Exception e) {

        }
        return map;
    }

    /**
     * jsonToMap
     *
     * @param jsonString
     * @param type
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> toComplexMap(String jsonString, Type type) {
        Map<K, V> map = null;
        try {
            map = getComplexGson().fromJson(jsonString, type);
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * jsonToList
     *
     * @param jsonString
     * @param type
     * @return
     */
    public static <T> List<T> toList(String jsonString, Type type) {
        List<T> list = null;
        try {
            list = gson.fromJson(jsonString, type);
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * jsonToObject
     *
     * @param jsonString
     * @param type
     * @return
     */
    public static <T> T toObject(String jsonString, Type type) {
        T t = null;
        try {
            t = gson.fromJson(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * jsonToArray
     *
     * @param jsonString
     * @param type
     * @return
     */
    public static <T> T[] toArray(String jsonString, Type type) {
        T[] ts = null;
        try {
            Gson gson = new Gson();
            ts = gson.fromJson(jsonString, type);
        } catch (Exception e) {
        }
        return ts;
    }

    /**
     * GsonBuilder
     *
     * @param params
     * @return
     */
    private static Gson getComplexGson(String... params) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss:SSS";
        double version = 1.0d;
        if (params != null && params.length > 0) {
            dateFormat = String.valueOf(params[0]);
            version = Double.valueOf(params[1]);
        }
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
                .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                .serializeNulls()  //导出值为null的字段
                .setDateFormat(dateFormat)//时间转化为特定格式
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
                .setPrettyPrinting() //对json结果格式化.
                .setVersion(version)    //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
                //@Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
                //@Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
                .create();
    }


}
