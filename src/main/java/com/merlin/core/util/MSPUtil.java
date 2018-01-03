package com.merlin.core.util;

import com.merlin.core.worker.sp.SPApp;
import com.merlin.core.worker.sp.SPUser;

/**
 * Created by ncm on 16/10/31.
 */

public class MSPUtil {

    public static SPUser user() {
        return SPUser.inst();
    }

    public static SPApp app() {
        return SPApp.inst();
    }

}
