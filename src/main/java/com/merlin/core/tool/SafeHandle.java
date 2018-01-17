package com.merlin.core.tool;

import android.os.Handler;
import android.os.Message;

import com.merlin.core.util.MLog;

import java.lang.ref.WeakReference;

/**
 * @author merlin
 */

public class SafeHandle<T> extends Handler {

    private final WeakReference<T> weakReference;
    private IHandler iHandler;

    public SafeHandle(T t, IHandler iHandler) {
        weakReference = new WeakReference<>(t);
        this.iHandler = iHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        if (weakReference.get() != null && isCanHandle()) {
            iHandler.onHandleMessage(msg);
        } else {
            MLog.e("activity is not finished or others, so can not handle message");
        }
    }

    private boolean isCanHandle() {
        return true;
    }

}
