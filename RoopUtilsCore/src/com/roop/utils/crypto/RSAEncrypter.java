package com.roop.utils.crypto;

import com.roop.utils.exception.handling.IExHandler;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.InvalidKeyException;

public final class RSAEncrypter extends RSADecrypter {
	
	public RSAEncrypter(RSAPair keys){
		super(keys);
	}

	public RSAEncrypter(RSAPair keys, boolean useStrongPadding){
		super(keys, useStrongPadding);
	}

	public RSAEncrypter(RSAPair keys, boolean useStrongPadding, IExHandler exh){
		super(keys, useStrongPadding, exh);
	}

	private final void initEnc() throws InvalidKeyException {
		mCipher.init(Cipher.ENCRYPT_MODE, mKeys.getEncodingKey());
	}

	public final synchronized byte[] encrypt(byte[] data) throws IOException, InvalidKeyException {
		this.initEnc();
		return this.cipher(data, mKeys.getKeySize() / 8 - 11);
	}
}
