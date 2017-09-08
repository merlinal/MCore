package com.merlin.core.base;

/**
 * Created by ncm on 16/11/14.
 */

public abstract class AbstractVM implements VmInterface {

    public AbstractVM() {
        this(true);
    }

    public AbstractVM(boolean isInit) {
        if (isInit) {
            initData();
        }
    }

}
