package com.roop.utils.hashing;

import com.roop.utils.Constants;
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
		public byte[] hash(byte[] in) {
			return hash(in, "SHA-256");
		}
		
		@Override
		public byte[] hash(InputStream in) throws IOException {
			return hash(in, "SHA-256");
		}

		@Override
		public byte[] hash(ByteBuffer in) {
			return hash(in, "SHA-256");
		}

		@Override
		public byte[] hash(ByteChannel in) throws IOException{
			return hash(in, "SHA-256");
		}

	},
	Sha512{
		
		@Override
		public byte[] hash(byte[] in) {
			return hash(in, "SHA-512");
		}

		@Override
		public byte[] hash(InputStream in) throws IOException {
			return hash(in, "SHA-512");
		}

		@Override
		public byte[] hash(ByteBuffer in) {
			return hash(in, "SHA-512");
		}

		@Override
		public byte[] hash(ByteChannel in) throws IOException {
			return hash(in, "SHA-512");
		}

	},
	Sha1{

		@Override
		public byte[] hash(byte[] in) {
			return hash(in, "SHA");
		}

		@Override
		public byte[] hash(InputStream in) throws IOException {
			return hash(in, "SHA");
		}

		@Override
		public byte[] hash(ByteBuffer in) {
			return hash(in, "SHA");
		}

		@Override
		public byte[] hash(ByteChannel in) throws IOException {
			return hash(in, "SHA");
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
		public byte[] hash(byte[] in) {
			synchronized (c) {
				c.reset();
				c.update(in);

				return toByte(c.getValue());
			}
		}

		@Override
		public byte[] hash(InputStream in) throws IOException {
//			return hash(Utils.read(in));
			synchronized (c) {
				c.reset();

				byte[] buf = new byte[Short.MAX_VALUE];
				int size;
				while((size = in.read(buf)) > 0) {
					c.update(buf, 0, size);
				}

				return toByte(c.getValue());
			}
		}

		@Override
		public byte[] hash(ByteBuffer in) {
			synchronized (c) {
				c.reset();

				while (in.hasRemaining()) {
					c.update(in.get());
				}

				return toByte(c.getValue());
			}
		}

		@Override
		public byte[] hash(ByteChannel in) throws IOException {
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
	public abstract byte[] hash(byte[] in);
	public abstract byte[] hash(InputStream in) throws IOException;
	public abstract byte[] hash(ByteBuffer in);
	public abstract byte[] hash(ByteChannel in) throws IOException;
	//abstracts end

	protected byte[] hash(byte[] data, String instance){
		try {
			return MessageDigest.getInstance(instance).digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	protected byte[] hash(ByteBuffer bb, String instance){
		try {
			MessageDigest dig = MessageDigest.getInstance(instance);

			dig.update(bb);

			return dig.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	protected byte[] hash(ByteChannel fc, String instance) throws IOException {
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
	protected byte[] hash(InputStream is, String instance) throws IOException{
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
	public byte[] hash(VFile file){
		try {
			byte[] out;
			FileInputStream fis = new FileInputStream(file.fullPath());
			out = this.hash(fis);
			fis.close();
			return out;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}

		return null;
	}
	*/

	public byte[] hash(File file) throws IOException{
		byte[] out;
		FileInputStream fis = new FileInputStream(file);
		out = this.hash(fis);
		fis.close();
		return out;
	}

	public byte[] hash(FileChannel in) throws IOException {
		return hash(in.map(FileChannel.MapMode.READ_ONLY, 0, in.size()));
	}

	public byte[] hash(Path in) throws IOException {
		return hash(FileChannel.open(in));
	}
	
	//encoded
	public String hash(File file, IByteUtil encoder) throws IOException {
		return encoder.getString(this.hash(file));
	}
	public String hash(byte[] data, IByteUtil encoder){
		return encoder.getString(this.hash(data));
	}
	//encoded end
}
