package com.merlin.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author merlin
 */

public class MDate {

    public final static String FORMAT_STANDARD = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_STANDARD_NUMBER = "yyyyMMddHHmmss";

    public static String format(String dateStr, String srcFormat, String targetFormat) {
        return format(parse(dateStr, srcFormat), targetFormat);
    }

    public static String format(Date date, String format) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            // dateFormat.setLenient(false);
            return dateFormat.format(date);
        }
        return "";
    }

    public static Date parse(String dateStr, String format) {
        if (dateStr != null) {
            if (dateStr.trim().length() < 1) {
                return null;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            // dateFormat.setLenient(false);
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
