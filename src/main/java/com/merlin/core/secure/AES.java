package com.merlin.core.secure;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密 & 解密
 */
public class AES {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    /* 加密模式和填充方式
     加密方式/工作模式/填充模式       16字节加密后数据长度        不满16字节加密后长度
     AES/CBC/NoPadding             16                          不支持
     AES/CBC/PKCS5Padding          32                          16
     AES/CBC/ISO10126Padding       32                          16
     AES/CFB/NoPadding             16                          原始数据长度
     AES/CFB/PKCS5Padding          32                          16
     AES/CFB/ISO10126Padding       32                          16
     AES/ECB/NoPadding             16                          不支持
     AES/ECB/PKCS5Padding          32                          16
     AES/ECB/ISO10126Padding       32                          16
     AES/OFB/NoPadding             16                          原始数据长度
     AES/OFB/PKCS5Padding          32                          16
     AES/OFB/ISO10126Padding       32                          16
     AES/PCBC/NoPadding            16                          不支持
     AES/PCBC/PKCS5Padding         32                          16
     AES/PCBC/ISO10126Padding      32                          16
     */

    /**
     * SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
     */
    private static final String SHA1PRNG = "SHA1PRNG";

    public static String encrypt(String content, String password) {
        /*
        加密后的 byte[] 不能通过toString或new String(byte[] bytes) 转换，
        否则会会抛异常javax.crypto.IllegalBlockSizeException: Input length must be multiple of 16 when decrypting with padded cipher
        需通过byte2Hex(byte[] bytes)来转换
        */
        return Hex.toHex(encryptToBytes(content, password));
    }

    /**
     * 加密
     *
     * @param content
     * @param password
     * @return
     */
    private static byte[] encryptToBytes(String content, String password) {
        try {
            KeyGenerator gen = KeyGenerator.getInstance(ALGORITHM);
            gen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = gen.generateKey();
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] byteContent = content.getBytes("utf-8");
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 加密
            return cipher.doFinal(byteContent);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content
     * @param password
     * @return
     */
    public static String decrypt(String content, String password) {
        byte[] bytes = decrypt(Hex.toByte(content), password);
        return bytes != null ? new String(bytes) : null;
    }

    /**
     * 解密
     *
     * @param content
     * @param password
     * @return
     */
    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator gen = KeyGenerator.getInstance(ALGORITHM);
            gen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = gen.generateKey();
            byte[] encodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 加密
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param content
     * @param password
     * @param mode     加密模式
     * @return
     */
    public static String encrypt(String content, String password, String mode) {
        if (isBlank(content)) {
            return content;
        }
        if (isBlank(mode)) {
            mode = TRANSFORMATION;
        }
        try {
            byte[] result = encrypt(content.getBytes(), password, mode);
            return result != null ? Hex.toHex(result) : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密
     *
     * @param content
     * @param password
     * @param mode
     * @return
     */
    private static byte[] encrypt(byte[] content, String password, String mode) {
        try {
            byte[] raw = getRawKey(password.getBytes());
            SecretKeySpec keySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密
     *
     * @param password
     * @param content
     * @param mode
     * @return
     */
    public static String decrypt(String content, String password, String mode) {
        if (isBlank(content)) {
            return "";
        }
        if (isBlank(mode)) {
            mode = TRANSFORMATION;
        }
        try {
            /*byte[] enc = Base64Decoder.decodeToBytes(content);*/
            byte[] result = decrypt(Hex.toByte(content), password, mode);
            return result != null ? new String(result) : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 解密
     *
     * @param content
     * @param password
     * @param mode
     * @return
     */
    private static byte[] decrypt(byte[] content, String password, String mode) {
        try {
            byte[] raw = getRawKey(password.getBytes());
            SecretKeySpec keySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes = new byte[20];
            localSecureRandom.nextBytes(bytes);
            return Hex.toHex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对密钥进行处理
     *
     * @param seed
     * @return
     */
    private static byte[] getRawKey(byte[] seed) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            //for android
            SecureRandom sr = null;
            // 在4.2以上版本中，SecureRandom获取方式发生了改变
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                sr = SecureRandom.getInstance(SHA1PRNG, "Crypto");
            } else {
                sr = SecureRandom.getInstance(SHA1PRNG);
            }
            // for Java
            /* secureRandom = SecureRandom.getInstance(SHA1PRNG); */
            sr.setSeed(seed);
            //256 bits or 128 bits,192bits
            kgen.init(128, sr);
            //AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return raw;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().length() < 1;
    }

    /*public static void main(String[] args) {
        String content = "hi，中国人民站起来了！";
        String password = "似懂非懂是公司分管";
        //加密
        System.out.println("加密前：" + content);
        String encryptResultStr1 = encrypt(content, password, TRANSFORMATION);
        String encryptResultStr2 = encrypt(content, password);
        System.out.println("加密后1：" + encryptResultStr1);
        System.out.println("加密后2：" + encryptResultStr2);
        //解密
        System.out.println("解密后1：" + decrypt(encryptResultStr1, password, TRANSFORMATION));
        System.out.println("解密后2：" + decrypt(encryptResultStr2, password));

//        EB9E55527FFD1334BC22F8BFBD477DCC4D85B95973A1EE916C87892E71E690832508F587505D9E800FE69C114D2A8931
    }*/


}
