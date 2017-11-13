package com.merlin.core.secure;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zal
 */
public class MD5 {

    /**
     * md5
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        MessageDigest messageDigest;
        StringBuffer md5StrBuff = null;
        try {
            md5StrBuff = new StringBuffer();
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            for (byte b : messageDigest.digest()) {
                md5StrBuff.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

}
