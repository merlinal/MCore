package com.merlin.core.worker.sp;

/**
 * Created by ncm on 16/10/31.
 */

public class SPUser extends SPBase {

    private final static String SP_NAME = "sp_user";

    public static SPUser inst() {
        return InstHolder.instance;
    }

    private static class InstHolder {
        private final static SPUser instance = new SPUser();
    }

    private SPUser() {
        super(SP_NAME);
    }

}
