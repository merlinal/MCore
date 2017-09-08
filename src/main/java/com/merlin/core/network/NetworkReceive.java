package com.merlin.core.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.merlin.core.context.AppContext;

/**
 * Created by ncm on 2017/5/4.
 */

public class NetworkReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppContext.inst().notifyNetWorkTypeChanged();
    }


}
