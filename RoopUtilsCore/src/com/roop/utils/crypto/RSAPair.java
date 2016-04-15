package com.roop.utils.crypto;

import java.security.*;
import java.security.interfaces.RSAKey;

public class RSAPair {

	private final KeyPair keys;
	private boolean usePukToEncrypt;


	public PrivateKey getPik() {
		return keys.getPrivate();
	}

	public PublicKey getPuk() {
		return keys.getPublic();
	}
	
	protected Key getEncodingKey(){
		return usePukToEncrypt ? getPuk() : getPik();
	}
	
	protected Key getDecodingKey(){
		return !usePukToEncrypt ? getPuk() : getPik();
	}
	
	public int getKeySize(){
		return ((RSAKey)(getPuk() != null ? getPuk() : getPik())).getModulus().bitLength();
	}
	
	public boolean usePukToEncrypt() {
		return usePukToEncrypt;
	}
	
	public void setUsePukToEncrypt(boolean usePukToEncrypt) {
		this.usePukToEncrypt = usePukToEncrypt;
	}

	private RSAPair(){
		this.keys = null;
		this.usePukToEncrypt = false;
	}

	public RSAPair(KeyPair keys){
		this(keys, false);
	}
	
	public RSAPair(KeyPair keys, boolean usePukToEncrypt){
		if(keys == null || keys.getPrivate() == null && !usePukToEncrypt && keys.getPublic() == null && usePukToEncrypt)
			throw new NullPointerException();
		
		this.keys = keys;
		this.usePukToEncrypt = usePukToEncrypt;
	}
	
	public RSAPair(PublicKey puk, PrivateKey prv, boolean usePukToEncrypt){
		this(new KeyPair(puk, prv), usePukToEncrypt);
	}
	
	public static RSAPair generate(){
		return generate(2048);
	}
	
	public static RSAPair generate(int keySize){
		SecureRandom random = new SecureRandom();
    	KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");	
			generator.initialize(keySize, random);
			return new RSAPair(generator.generateKeyPair());
		} catch (NoSuchAlgorithmException e) {}
		
		return null;
	}
}
