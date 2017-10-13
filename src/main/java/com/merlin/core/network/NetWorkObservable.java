package com.merlin.core.network;

import android.database.Observable;

import com.merlin.core.context.DeviceInfo;

/**
 * Created by Administrator on 2017/9/22.
 */

public class NetWorkObservable extends Observable<NetWorkObserver> {

    private NetWorkType lastType;

    public void notifyChanged() {
        if (mObservers.size() < 1) {
            return;
        }
        NetWorkType nowType = DeviceInfo.inst().getNetWorkType();
        if (lastType != nowType) {
            lastType = nowType;
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onNetWorkChanged(nowType);
                }
            }
        }
    }

    public int getObserverSize() {
        return mObservers.size();
    }

}
