package com.merlin.core.util;

import android.util.Log;

import com.merlin.core.context.BaseApp;
import com.merlin.core.context.MContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ncm on 16/11/7.
 */
public class MLog {

    public static void v(String content) {
        if (MContext.isDebug()) {
            Log.d(generateTag(), content);
        }
    }

    public static void d(String content) {
        if (MContext.isDebug()) {
            Log.d(generateTag(), content);
        }
    }

    public static void i(String content) {
        if (MContext.isDebug()) {
            Log.i(generateTag(), content);
        }
    }

    public static void w(String content) {
        if (MContext.isDebug()) {
            Log.w(generateTag(), content);
        }
    }

    public static void e(String content) {
        if (MContext.isDebug()) {
            Log.e(generateTag(), content);
        }
    }

    public static void wtf(Throwable t) {
        if (MContext.isDebug()) {
            Log.wtf(generateTag(), t);
        }
    }

    public static void wtf(String content) {
        if (MContext.isDebug()) {
            Log.wtf(generateTag(), content);
        }
    }

    public static void json(String content) {
        if (MContext.isDebug()) {
            printJson(generateTag(), content);
        }
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[5];
    }

    private static String generateTag() {
        String tag = "%s.%s(L:%d)";
        StackTraceElement caller = getCallerStackTraceElement();
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }

    private static void printJson(String tag, String msg) {
        String message;
        try {
            if (msg.startsWith("{")) {
                message = new JSONObject(msg).toString(4);
            } else if (msg.startsWith("[")) {
                message = new JSONArray(msg).toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        String[] lines = message.split(System.getProperty("line.separator"));
        blockStart(tag);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        blockEnd(tag);
    }

    private static void blockStart(String tag) {
        Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
    }

    private static void blockEnd(String tag) {
        Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
    }

}
