package com.merlin.core.worker.sp;

/**
 * Created by ncm on 16/10/31.
 */

public class SPApp extends SPBase {

    private final static String SP_NAME = "sp_app";

    public static SPApp inst() {
        return InstHolder.instance;
    }

    private static class InstHolder {
        private final static SPApp instance = new SPApp();
    }

    private SPApp() {
        super(SP_NAME);
    }

}
