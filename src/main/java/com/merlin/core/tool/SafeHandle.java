package com.merlin.core.tool;

import android.os.Handler;
import android.os.Message;

import com.merlin.core.util.MLog;

import java.lang.ref.WeakReference;

/**
 * @author merlin
 */

public abstract class SafeHandle<T> extends Handler {

    private final WeakReference<T> weakReference;

    public SafeHandle(T t) {
        weakReference = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        if (weakReference.get() != null && isCanHandle()) {
            onHandleMessage(msg);
        } else {
            MLog.e("activity is not finished or others, so can not handle message");
        }
    }

    protected abstract void onHandleMessage(Message msg);

    private boolean isCanHandle() {
        return true;
    }

}
