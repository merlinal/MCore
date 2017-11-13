package com.merlin.core.secure;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RSA {

//	public static final String ALGORITHM_RSA = "RSA";
//	public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
//	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
//	public static final String PUBLIC_KEY = "PublicKey";
//	public static final String PRIVATE_KEY = "PrivateKey";
//	public static final int MAX_DECRYPT_BLOCK = 128;
//	public static final int MAX_ENCRYPT_BLOCK = 117;
//
//	public static Map<String, Object> generateKeyPair() throws NoSuchAlgorithmException {
//		KeyPairGenerator localKeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
//		localKeyPairGenerator.initialize(1024);
//		KeyPair localKeyPair = localKeyPairGenerator.generateKeyPair();
//		RSAPublicKey localRSAPublicKey = (RSAPublicKey) localKeyPair.getPublic();
//		RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey) localKeyPair.getPrivate();
//		HashMap<String, Object> localHashMap = new HashMap<String, Object>(2);
//		localHashMap.put(PUBLIC_KEY, localRSAPublicKey);
//		localHashMap.put(PRIVATE_KEY, localRSAPrivateKey);
//		return localHashMap;
//	}
//
//	public static String getPublicKey(Map<String, Object> paramMap)
//			throws UnsupportedEncodingException {
//		Key localKey = (Key) paramMap.get(PUBLIC_KEY);
//		return Base64.encodeToString(localKey.getEncoded());
//	}
//
//	public static String getPrivateKey(Map<String, Object> paramMap)
//			throws Exception {
//		Key localKey = (Key) paramMap.get(PRIVATE_KEY);
//		return Base64.encodeToString(localKey.getEncoded());
//	}
//
//	public static String sign(byte[] data, String privateKey) throws Exception {
//		byte[] keyBytes = Base64.decode(privateKey);
//		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//		signature.initSign(privateK);
//		signature.update(data);
//		return Base64.encodeToString(signature.sign());
//	}
//
//	public static boolean verify(byte[] data, String publicKey, String sign)
//			throws Exception {
//		byte[] keyBytes = Base64.decode(publicKey);
//		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PublicKey publicK = keyFactory.generatePublic(keySpec);
//		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//		signature.initVerify(publicK);
//		signature.update(data);
//		return signature.verify(Base64.decode(sign));
//	}
//
//	public static byte[] decryptByPrivateKey(byte[] encryptedData,
//			String privateKey) throws Exception {
//		byte[] arrayOfByte = Base64.decode(privateKey);
//		PKCS8EncodedKeySpec localPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(
//				arrayOfByte);
//		KeyFactory localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PrivateKey localPrivateKey = localKeyFactory
//				.generatePrivate(localPKCS8EncodedKeySpec);
//		Cipher localCipher = Cipher.getInstance(TRANSFORMATION);
//		localCipher.init(2, localPrivateKey);
//
//		return crypt(encryptedData, localCipher, 128);
//	}
//
//	public static byte[] decryptByPublicKey(byte[] encryptedData,
//			String publicKey) throws Exception {
//		byte[] arrayOfByte = Base64.decode(publicKey);
//		X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(
//				arrayOfByte);
//		KeyFactory localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PublicKey localPublicKey = localKeyFactory
//				.generatePublic(localX509EncodedKeySpec);
//		Cipher localCipher = Cipher.getInstance(TRANSFORMATION);
//		localCipher.init(2, localPublicKey);
//
//		return crypt(encryptedData, localCipher, 128);
//	}
//
//	public static byte[] encryptByPrivateKey(byte[] dataTobeEncrypt,
//			String privateKey) throws Exception {
//		byte[] privateKeyBytes = Base64.decode(privateKey);
//		KeyFactory localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PrivateKey localPrivateKey = localKeyFactory
//				.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
//		Cipher localCipher = Cipher.getInstance(TRANSFORMATION);
//		localCipher.init(1, localPrivateKey);
//
//		return crypt(dataTobeEncrypt, localCipher, 117);
//	}
//
//	public static byte[] encryptByPublicKey(byte[] dataTobeEncrypt,
//			String publicKey) throws Exception {
//		byte[] publicKeyBytes = Base64.decode(publicKey);
//		KeyFactory localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		PublicKey localPublicKey = localKeyFactory
//				.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
//		Cipher localCipher = Cipher.getInstance(TRANSFORMATION);
//		localCipher.init(1, localPublicKey);
//
//		return crypt(dataTobeEncrypt, localCipher, 117);
//	}
//
//	private static byte[] crypt(byte[] data, Cipher localCipher, int maxBlock)
//			throws Exception {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		int length = data.length;
//		int cryptLength = 0;
//
//		while (cryptLength < length) {
//			int unCryptLength = length - cryptLength;
//			int block = Math.min(maxBlock, unCryptLength);
//			byte[] cache = localCipher.doFinal(data, cryptLength, block);
//			out.write(cache, 0, cache.length);
//			cryptLength += block;
//		}
//		byte[] cryptedData = out.toByteArray();
//		out.close();
//		return cryptedData;
//	}
//
//	public static RSAPublicKey generateRSAPublicKey(
//			BigInteger paramBigInteger1, BigInteger paramBigInteger2)
//			throws Exception {
//		KeyFactory localKeyFactory = null;
//		try {
//			localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
//			throw new Exception(localNoSuchAlgorithmException.getMessage());
//		}
//		RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(
//				paramBigInteger1, paramBigInteger2);
//		try {
//			return ((RSAPublicKey) localKeyFactory
//					.generatePublic(localRSAPublicKeySpec));
//		} catch (InvalidKeySpecException localInvalidKeySpecException) {
//			throw new Exception(localInvalidKeySpecException.getMessage());
//		}
//	}
//
//	public static RSAPrivateKey generateRSAPrivateKey(
//			BigInteger paramBigInteger1, BigInteger paramBigInteger2)
//			throws Exception {
//		KeyFactory localKeyFactory = null;
//		try {
//			localKeyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
//			throw new Exception(localNoSuchAlgorithmException.getMessage());
//		}
//		RSAPrivateKeySpec localRSAPrivateKeySpec = new RSAPrivateKeySpec(
//				paramBigInteger1, paramBigInteger2);
//		try {
//			return ((RSAPrivateKey) localKeyFactory
//					.generatePrivate(localRSAPrivateKeySpec));
//		} catch (InvalidKeySpecException localInvalidKeySpecException) {
//			throw new Exception(localInvalidKeySpecException.getMessage());
//		}
//	}

}
