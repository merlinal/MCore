package com.merlin.core.worker;

import com.merlin.core.worker.sp.SPApp;
import com.merlin.core.worker.sp.SPUser;

/**
 * Created by ncm on 16/10/31.
 */

public class SPWorker {

    public static SPUser user() {
        return SPUser.getInstance();
    }

    public static SPApp app() {
        return SPApp.getInstance();
    }

}
