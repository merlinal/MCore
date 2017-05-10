package com.merlin.core.tool;

import android.os.Handler;
import android.os.Message;

import com.merlin.core.util.LogUtil;

/**
 * Created by ncm on 16/11/30.
 */

public abstract class SafeHandle extends Handler {

    @Override
    public void handleMessage(Message msg) {
        if (isCanHandle()) {
            safeHandleMessage(msg);
        } else {
            LogUtil.e("activity is not finished or others, so can not handle message");
        }
    }

    protected abstract void safeHandleMessage(Message msg);

    private boolean isCanHandle() {
        return true;
    }

}
