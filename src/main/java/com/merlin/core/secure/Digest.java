package com.merlin.core.secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Digest {
	public static final String MD5 = "md5";
	public static final String SHA1 = "SHA-1";
	public static final String HmacSHA1 = "HmacSHA1";

	private static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private static MessageDigest getMd5Digest() {
		return getDigest(MD5);
	}

	private static MessageDigest getShaDigest() {
		return getDigest(SHA1);
	}

	public static byte[] md5(byte[] data) {
		return getMd5Digest().digest(data);
	}

	public static String md5File(File file) {
		String md5 = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[4096];
			int size = 0;
			MessageDigest md = getMd5Digest();
			while ((size = fileInputStream.read(buffer)) != -1) {
				md.update(buffer, 0, size);
			}
			md5 = new String(Hex.toHexChar(md.digest()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return md5;
	}

	public static byte[] md5(String data) {
		return md5(data.getBytes());
	}

	public static String md5Hex(byte[] data) {
		return new String(Hex.toHexChar(md5(data)));
	}

	public static String md5Hex(String data) {
		return new String(Hex.toHexChar(md5(data)));
	}

	public static byte[] sha(byte[] data) {
		return getShaDigest().digest(data);
	}

	public static byte[] sha(String data) {
		return sha(data.getBytes());
	}

	public static String shaHex(byte[] data) {
		return new String(Hex.toHexChar(sha(data)));
	}

	public static String shaHex(String data) {
		return new String(Hex.toHexChar(sha(data)));
	}

	public static String hmacSHA1(String base, String key) {
		try {
			Mac mac = Mac.getInstance(SHA1);

			if (key != null) {
				SecretKeySpec spec = new SecretKeySpec(key.getBytes(), HmacSHA1);
				mac.init(spec);
			}

			byte[] bytes = mac.doFinal(base.getBytes());
			return new String(Hex.toHexChar(bytes));
		} catch (Exception localException) {
		}
		return null;
	}
	
}
