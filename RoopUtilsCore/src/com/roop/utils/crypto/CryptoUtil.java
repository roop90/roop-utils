package com.roop.utils.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.*;

public abstract class CryptoUtil {
	protected CryptoUtil(){
	}

	public static byte[] hardenPassword(String pw, byte[] salt, int iterations, int keylen) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PBEKeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, iterations, keylen);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		return skf.generateSecret(spec).getEncoded();
	}

	public static byte[] createSalt(int size) throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[size];
		sr.nextBytes(salt);
		return salt;
	}

	public static SSLContext createSSLContext(URL jks, char[] pw){
		return createSSLContext(jks, pw, false);
	}

	public static SSLContext createSSLContext(URL jks, char[] pw, boolean client){
		try{
			return createSSLContext(jks.openStream(), pw, client);
		} catch (IOException e) {
			e.printStackTrace();  //TODO: For Debug only
		}

		return null;
	}

	public static SSLContext createSSLContext(File jks, char[] pw){
		return createSSLContext(jks, pw, false);
	}

	public static SSLContext createSSLContext(File jks, char[] pw, boolean client){
		try {
			return createSSLContext(new FileInputStream(jks), pw, client);
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //TODO: For Debug only
		}

		return null;
	}

	public static SSLContext createSSLContext(InputStream in, char[] pw, boolean client){
		try {
			SSLContext cont = SSLContext.getInstance("TLSv1.2");
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, pw);

			if(!client) {
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, pw);

				cont.init(kmf.getKeyManagers(), null, null);
			} else{
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(ks);

				cont.init(null, tmf.getTrustManagers(), null);
			}

			return cont;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (KeyStoreException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (CertificateException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (IOException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (KeyManagementException e) {
			e.printStackTrace();  //TODO: For Debug only
		} finally {
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {}
		}

		return null;
	}

	/*
	public static RSAPair getRsaPairFromBytes(byte[] bPuk, byte[] bPik){
		return new RSAPair(getPubKeyFromData(bPuk), getPrvKeyFromData(bPik), false);
	}
	
	public static RSAPair getRsaPairFromJKS(File fJKS, char[] pw, String alias){
		FileInputStream fis = null;
		RSAPair out = null;
		
		try {
			fis = new FileInputStream(fJKS);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(fis, pw);
			PrivateKey pik = (PrivateKey)ks.getKey(alias, pw);
			PublicKey puk = ks.getCertificate(alias).getPublicKey();
			out = new RSAPair(new KeyPair(puk, pik));
		} catch (NoSuchAlgorithmException e) {
			// TODO for debug
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO for debug
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO for debug
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO for debug
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //TODO: For Debug only
		} catch (IOException e) {
			e.printStackTrace();  //TODO: For Debug only
		} finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {}
		}
		
		return out;
	}
	*/
	
	public static PublicKey getPubKeyFromData(byte[] bKey){
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bKey);
			return kf.generatePublic(publicKeySpec);
		} catch (Exception e) {}
		return null;
	}
	
	public static PrivateKey getPrvKeyFromData(byte[] bKey){
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bKey);
			return kf.generatePrivate(privateKeySpec);
		} catch (Exception e) {}
		return null;
	}

	public static PublicKey getPubKeyFromPrvKey(PrivateKey pKey){
		if(pKey instanceof RSAPrivateCrtKey){
			RSAPrivateCrtKey key = (RSAPrivateCrtKey) pKey;
			RSAPublicKeySpec pSpec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
			try {
				return KeyFactory.getInstance("RSA").generatePublic(pSpec);
			} catch (InvalidKeySpecException e) {
				//ignore
			} catch (NoSuchAlgorithmException e) {
				//ignore
			}
		}
		return null;
	}
}
