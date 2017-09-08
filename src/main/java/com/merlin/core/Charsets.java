package com.merlin.core;


import java.nio.charset.Charset;

/**
 * Created by ncm on 16/12/9.
 */

public class Charsets {

    public static final String UTF_8 = "UTF-8";

    public static Charset get(String charsetName) {
        return Charset.forName(charsetName);
    }

}
