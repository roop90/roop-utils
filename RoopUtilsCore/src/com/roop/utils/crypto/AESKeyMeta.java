package com.roop.utils.crypto;

import java.io.Serializable;
import java.security.SecureRandom;

public class AESKeyMeta implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2178440585381063891L;

	private final byte[] mSalt;
	private final byte[] mIV;
	private transient String pw = null;

	public final byte[] getSalt(){
		return mSalt;
	}

	public final byte[] getIV(){
		return mIV;
	}

	public final String getPW(){
		return pw;
	}

	public final void setPW(String pw){
		this.pw = pw;
	}

	public AESKeyMeta(byte[] salt, byte[] IV){
		mSalt = salt;
		mIV = IV;
	}

	/*
	@Override
	public final String toString() {
		IByteUtil coder = AByteUtil.getDefaultEnum("base64");
		return coder.getString(mSalt) + " - " + coder.getString(mIV);
	}*/

	public static AESKeyMeta generate(){
//		ByteBuffer bf = ByteBuffer.allocate(80);
//		ByteBuffer bfTemp = ByteBuffer.allocate(8);
//
//		for(int i =0; i<10;i++){
//			Random r = new Random();
//			bfTemp.clear();
//			bfTemp.putLong(System.nanoTime());
//			bfTemp.flip();
//			while(bfTemp.remaining() > 0){
//				bf.put(r.nextInt(80), bfTemp.get());
//				bf.rewind();
//			}
//		}
		//bf.flip();

		//System.out.println(bf.asCharBuffer().toString());

		SecureRandom sec = new SecureRandom();
		//SecureRandom sec2 = new SecureRandom(bf.array());
		byte[] salt = new byte[16];
		byte[] iv = new byte[16];

		sec.nextBytes(salt);
		sec.nextBytes(iv);

		return new AESKeyMeta(salt, iv);
	}
}
