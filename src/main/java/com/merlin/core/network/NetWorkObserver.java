package com.merlin.core.network;

/**
 * Created by ncm on 2017/5/5.
 */

public interface NetWorkObserver {

    /**
     * 网络状态改变
     *
     * @param type
     */
    void onNetWorkChanged(NetWorkType type);

}
