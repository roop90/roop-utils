package com.roop.utils.crypto;

import com.roop.utils.Constants;
import com.roop.utils.exception.handling.IExHandler;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;

public class RSADecrypter {
	protected final RSAPair mKeys;
	protected Cipher mCipher;

	public RSADecrypter(RSAPair keys){
		this(keys, true);
	}

	public RSADecrypter(RSAPair keys, boolean useStrongPadding){
		this(keys, useStrongPadding, Constants.EXH);
	}

	public RSADecrypter(RSAPair keys, boolean useStrongPadding, IExHandler exh){
		try {
//			mCipher = Cipher.getInstance("RSA");
			//			mCipher = Cipher.getInstance("RSA/CBC/OAEPWithSHA-1AndMGF1Padding");

			if(useStrongPadding)
				mCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
			else
				mCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		} catch (Exception e) {
			//this case should NEVER happen (java demands that the Ciphers above are included)
			exh.exceptionCaught(e, this);
		}

		mKeys = keys;
	}

	protected final synchronized byte[] cipher(byte[] data, int blocksize) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try{
			int length = data.length;
			for (int i = 0; i < length; i += blocksize) {
				int read = i + blocksize < length ? blocksize : length - i;

				out.write(mCipher.doFinal(data, i, read));
			}
		} catch (Exception e) {
			throw new IOException("RSA operation failed");
		}

		return out.toByteArray();
	}

	private final void initDec() throws InvalidKeyException {
		mCipher.init(Cipher.DECRYPT_MODE, mKeys.getDecodingKey());
	}

//	public final void update(byte[] data){
//		mCipher.update(data);
//	}
//
//	public final byte[] doFinal() throws IOException {
//		try {
//			return mCipher.doFinal();
//		} catch (Exception e) {
//			throw new IOException("RSA operation failed");
//		}
//	}
//
//	public final byte[] doFinal(byte[] data) throws IOException {
//		try {
//			return mCipher.doFinal(data);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new IOException("RSA operation failed");
//		}
//	}

	public final synchronized byte[] decrypt(byte[] data) throws IOException, InvalidKeyException{
		this.initDec();
		return this.cipher(data, mKeys.getKeySize() / 8);
	}
}
