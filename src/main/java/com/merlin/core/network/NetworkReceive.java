package com.merlin.core.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.merlin.core.context.MContext;

/**
 * @author merlin
 */

public class NetworkReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MContext.inst().notifyNetWorkTypeChanged();
    }

}
