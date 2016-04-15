package com.roop.utils.hashing;

import com.roop.utils.Constants;
import com.roop.utils.Utils;
import com.roop.utils.binary.ByteUtil;
import com.roop.utils.binary.IByteUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public enum HashUtil {
	Sha256{
		
		@Override
		public byte[] doHashing(byte[] in) {
			return doHashing(in, "SHA-256");
		}
		
		@Override
		public byte[] doHashing(InputStream in) throws IOException {
			return doHashing(in, "SHA-256");
		}

		@Override
		public byte[] doHashing(ByteBuffer in) {
			return doHashing(in, "SHA-256");
		}

		@Override
		public byte[] doHashing(ByteChannel in) throws IOException{
			return doHashing(in, "SHA-256");
		}

	},
	Sha512{
		
		@Override
		public byte[] doHashing(byte[] in) {
			return doHashing(in, "SHA-512");
		}

		@Override
		public byte[] doHashing(InputStream in) throws IOException {
			return doHashing(in, "SHA-512");
		}

		@Override
		public byte[] doHashing(ByteBuffer in) {
			return doHashing(in, "SHA-512");
		}

		@Override
		public byte[] doHashing(ByteChannel in) throws IOException {
			return doHashing(in, "SHA-512");
		}

	},
	Sha1{

		@Override
		public byte[] doHashing(byte[] in) {
			return doHashing(in, "SHA");
		}

		@Override
		public byte[] doHashing(InputStream in) throws IOException {
			return doHashing(in, "SHA");
		}

		@Override
		public byte[] doHashing(ByteBuffer in) {
			return doHashing(in, "SHA");
		}

		@Override
		public byte[] doHashing(ByteChannel in) throws IOException {
			return doHashing(in, "SHA");
		}

	},
	Crc32{
		final CRC32 c = new CRC32();			//only one crc32 class to keep object creation low
												//needs syncronization to work... shame on that

		private byte[] toByte(long val){		//todo make it better?
			String hex = Long.toHexString(c.getValue());
			hex = hex.length() %2 == 0 ? hex : "0"+hex;

			return ByteUtil.Hex.getBytes(hex);
		}

		@Override
		public byte[] doHashing(byte[] in) {
			synchronized (c) {
				c.reset();
				c.update(in);

				return toByte(c.getValue());
			}
		}

		@Override
		public byte[] doHashing(InputStream in) throws IOException {
			return doHashing(Utils.read(in));
		}

		@Override
		public byte[] doHashing(ByteBuffer in) {
			synchronized (c) {
				c.reset();

				while (in.hasRemaining()) {
					c.update(in.get());
				}

				return toByte(c.getValue());
			}
		}

		@Override
		public byte[] doHashing(ByteChannel in) throws IOException {
			synchronized (c) {
				c.reset();

				ByteBuffer buff = ByteBuffer.allocate(Constants.BUFF_M);

				while(in.read(buff) > -1){
					buff.flip();
					c.update(buff.array());
					buff.clear();
				}

				return toByte(c.getValue());
			}
		}
	};

	//abstracts
	public abstract byte[] doHashing(byte[] in);
	public abstract byte[] doHashing(InputStream in) throws IOException;
	public abstract byte[] doHashing(ByteBuffer in);
	public abstract byte[] doHashing(ByteChannel in) throws IOException;
	//abstracts end

	protected byte[] doHashing(byte[] data, String instance){
		try {
			return MessageDigest.getInstance(instance).digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	protected byte[] doHashing(ByteBuffer bb, String instance){
		try {
			MessageDigest dig = MessageDigest.getInstance(instance);

			dig.update(bb);

			return dig.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	protected byte[] doHashing(ByteChannel fc, String instance) throws IOException {
		try {
			MessageDigest dig = MessageDigest.getInstance(instance);

			ByteBuffer buff = ByteBuffer.allocateDirect(Constants.BUFF_L);

			while (fc.read(buff) > -1) {
				buff.flip();
				dig.update(buff);
				buff.clear();
			}

			return dig.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	protected byte[] doHashing(InputStream is, String instance) throws IOException{
		try {
			MessageDigest dig = MessageDigest.getInstance(instance);

			byte[] buf = new byte[Short.MAX_VALUE];
			int size;
			while((size = is.read(buf)) > 0) {
				dig.update(buf, 0, size);
			}

			return dig.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	public byte[] doHashing(VFile file){
		try {
			byte[] out;
			FileInputStream fis = new FileInputStream(file.fullPath());
			out = this.doHashing(fis);
			fis.close();
			return out;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}

		return null;
	}
	*/

	public byte[] doHashing(File file){
		try {
			byte[] out;
			FileInputStream fis = new FileInputStream(file);
			out = this.doHashing(fis);
			fis.close();
			return out;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
		
		return null;
	}

	public byte[] doHashing(FileChannel in){
		try {
			return doHashing(in.map(FileChannel.MapMode.READ_ONLY, 0, in.size()));
		} catch (IOException e) {}

		return null;
	}

	public byte[] doHashing(Path in){
		try {
			return doHashing(FileChannel.open(in));
		} catch (IOException e) {}

		return null;
	}
	
	//encoded
	public String doHashing(File file, IByteUtil encoder){
		return encoder.getString(this.doHashing(file));
	}
	public String doHashing(byte[] data, IByteUtil encoder){
		return encoder.getString(this.doHashing(data));
	}
	//encoded end
}
