package com.roop.utils.crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public final class AES {
	
    private final int mIterations;
    private final int mKeylen;
	private Cipher mCipher = null;
	private AESKeyMeta mKeyMeta = AESKeyMeta.generate();
	
	public AESKeyMeta getKeyMeta(){
		return mKeyMeta;
	}

	public void setKeyMeta(AESKeyMeta akm){
		if(akm != null)
			mKeyMeta = akm;
	}
	
	/**
	 * Uses 128bit AES for compatibility reasons
	 */
	public AES(){
		this(128);
	}
	
	/**
	 * @param keylength
	 * For stronger keys you will need Java Cryptography Extension (JCE).
	 * On Android 256bit keys are possible.
	 */
	public AES(int keylength){
		this(keylength, 4096);
	}
	
	public AES(int keylength, int iterations){

		mKeylen = keylength;
		mIterations = iterations;
		
		try {
//			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		} catch (Exception e) {
			System.out.println("Fallback to PKCS5Padding");
			try {
				mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public AES(int keylength, int iterations, String cipher) throws NoSuchPaddingException, NoSuchAlgorithmException {
		mKeylen = keylength;
		mIterations = iterations;

		mCipher = Cipher.getInstance(cipher);
	}
	
	private SecretKey createKey() throws GeneralSecurityException{
		if(mKeyMeta.getPW() == null || mKeyMeta.getPW().isEmpty()){
			throw new GeneralSecurityException();
		}
        SecretKeyFactory factory = null;
        SecretKey tmp = null;
        
        try {
//			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		} catch (NoSuchAlgorithmException e) {
        	throw new RuntimeException(e);
		}
        
        KeySpec spec = new PBEKeySpec(mKeyMeta.getPW().toCharArray(), mKeyMeta.getSalt(), mIterations, mKeylen);

		mKeyMeta.setPW(null);			//clear pw!!

        try {
			assert factory != null;
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
        	throw new RuntimeException(e);
		}

		assert tmp != null;
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}

	//streams

	public CipherInputStream createInputStream(InputStream in) throws Exception{
		if(in == null){
			throw new IllegalArgumentException("Stream must not be null!");
		}else if(mKeyMeta == null  || mKeyMeta.getPW() == null || mKeyMeta.getPW().isEmpty()){
			throw new GeneralSecurityException("KeyMeta invalid");
		}

		Cipher c = Cipher.getInstance("AES/CTR/PKCS5Padding");
		SecretKey key = this.createKey();
		c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(mKeyMeta.getIV()));

		return new CipherInputStream(in, c);
	}

	public CipherOutputStream createOutputStream(OutputStream out) throws Exception{
		if(out == null){
			throw new IllegalArgumentException("Stream must not be null!");
		}else if(mKeyMeta == null  || mKeyMeta.getPW() == null || mKeyMeta.getPW().isEmpty()){
			throw new GeneralSecurityException("KeyMeta invalid");
		}

		//PKCS5Padding
		Cipher c = Cipher.getInstance("AES/CTR/PKCS5Padding");
		SecretKey key = this.createKey();
		c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(mKeyMeta.getIV()));

		return new CipherOutputStream(out, c);
	}

	//streams end

	//encrypt
	public synchronized byte[] encrypt(byte[] data) throws GeneralSecurityException{
		if(data == null){
			throw new GeneralSecurityException("Data invalid");
		}else if(mKeyMeta == null  || mKeyMeta.getPW() == null || mKeyMeta.getPW().isEmpty()){
			throw new GeneralSecurityException("KeyMeta invalid");
		}

		SecretKey key = this.createKey();
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(mKeyMeta.getIV()));
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		return mCipher.doFinal(data);
	}

	public synchronized byte[] encrypt(byte[] data, AESKeyMeta akm) throws GeneralSecurityException {
		mKeyMeta = akm;
		return this.encrypt(data);
	}

	public synchronized byte[] encrypt(byte[] data, String pw, boolean newAKM) throws GeneralSecurityException {
		AESKeyMeta akm = newAKM ? AESKeyMeta.generate() : mKeyMeta;
		mKeyMeta.setPW(pw);
		return this.encrypt(data, akm);
	}

	public byte[] encrypt(byte[] data, String pw) throws GeneralSecurityException {
		return this.encrypt(data, pw, false);
	}
	//encrypt end


	//decrypt
	public synchronized byte[] decrypt(byte[] data) throws GeneralSecurityException {
		if(data == null){
			throw new GeneralSecurityException("Data invalid");
		}else if(mKeyMeta == null  || mKeyMeta.getPW() == null || mKeyMeta.getPW().isEmpty()){
			throw new GeneralSecurityException("KeyMeta invalid");
		}

		SecretKey key = this.createKey();
		try {
			mCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(mKeyMeta.getIV()));
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		return mCipher.doFinal(data);
	}

	public synchronized byte[] decrypt(byte[] data, AESKeyMeta akm) throws GeneralSecurityException {
		mKeyMeta = akm;
		return decrypt(data);
	}
	
	public synchronized byte[] decrypt(byte[] data, String pw) throws GeneralSecurityException {
		mKeyMeta.setPW(pw);
		return decrypt(data);
	}

}
