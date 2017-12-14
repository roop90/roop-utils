package com.roop.utils;

import com.roop.utils.binary.ByteUtil;
import com.roop.utils.binary.IByteUtil;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by roop on 12.08.2015.
 */
public class Utils {
	public interface ICopyCallback {
		/**
		 *
		 * @param copiedBytes last copied data in bytes
		 * @return true to interrupt copy
		 */
		public boolean onProgress(int copiedBytes);
		public void onException(IOException e);
		public void onFinish();
	}

	protected static final String NO_DATA = "No Data";
	protected static final int DEFAULT_BS = 1024*1024*5;
	protected static final IByteUtil DEFAULT_CODER = ByteUtil.Base64;

	protected Utils(){
	}

	//simple copy with input-/outputstream
	public static void copy(InputStream in, OutputStream out, long limit) throws IOException {

		long count = 0;
		final ReadableByteChannel src = Channels.newChannel(in);
		final WritableByteChannel dest = Channels.newChannel(out);

		final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BS);

		int read;
		do{
			read = src.read(buffer);

			buffer.flip();
			count+=buffer.remaining();

			dest.write(buffer);

			buffer.compact();
		}while(read != -1 && count < limit);

		buffer.flip();

		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}

	public static void copy(InputStream in, OutputStream out) throws IOException{
		final ReadableByteChannel src = Channels.newChannel(in);
		final WritableByteChannel dest = Channels.newChannel(out);

		final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BS);
		while (src.read(buffer) != -1) {

			buffer.flip();

			dest.write(buffer);

			buffer.compact();
		}

		buffer.flip();

		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}

	public static void copy(InputStream in, OutputStream out, ICopyCallback cb) {
		final ReadableByteChannel src = Channels.newChannel(in);
		final WritableByteChannel dest = Channels.newChannel(out);

		final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BS);

		try {

			long t0 = System.currentTimeMillis();
			while (src.read(buffer) != -1) {

				buffer.flip();

				dest.write(buffer);

				int data = buffer.position();

				buffer.compact();

				if(System.currentTimeMillis() - t0 > 100) {
					t0 = System.currentTimeMillis();
					if (cb.onProgress(data))
						return;
				}
			}

			buffer.flip();

			while (buffer.hasRemaining()) {
				dest.write(buffer);
			}

			cb.onProgress(buffer.position());

			cb.onFinish();
		} catch (IOException e) {
			cb.onException(e);
		}
	}


	public static void copy(ByteBuffer in, ByteBuffer out) {
		out.put(in);
	}
	//simple copy END


	//normal R/W
	//normal Stream
	public static void write(OutputStream os, byte[] data) throws IOException{
		if(data == null)
			throw new IOException(NO_DATA);

		final ByteArrayInputStream bis = new ByteArrayInputStream(data);

		copy(bis, os);
	}

	public static byte[] read(InputStream is) throws IOException{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		copy(is, bos);

		return bos.toByteArray();
	}
	//normal Stream end

	//normal File
	public static void write(File f, byte[] data) throws IOException{
		if(data == null)
			throw new IOException(NO_DATA);

		final FileOutputStream fos = new FileOutputStream(f);
		write(fos, data);
		fos.close();
	}

	public static byte[] read(File f) throws IOException {
		final FileInputStream fis = new FileInputStream(f);

		final byte[] data = read(fis);

		fis.close();

		return data;
	}
	//normal File end

	//normal URL
	public static byte[] read(URL url) throws IOException{
		final InputStream is = url.openStream();

		final byte[] data = read(is);

		is.close();

		return data;
	}
	//normal URL end

	//normal URI
	public static byte[] read(URI uri) throws IOException{
		return read(uri.toURL());
	}
	//normal URI end
	//normal R/W end


	//encoded R/W
	//encoded Stream
	public static void writeEncoded(OutputStream os, byte[] data) throws IOException{
		writeEncoded(os, data, DEFAULT_CODER);
	}

	public static void writeEncoded(OutputStream os, byte[] data, IByteUtil enc) throws IOException{
		if(data == null)
			throw new IOException(NO_DATA);

		data = ByteUtil.UTF8.getBytes(enc.getString(data));
		write(os, data);
	}

	public static byte[] readEncoded(InputStream is) throws IOException{
		return readEncoded(is, DEFAULT_CODER);
	}

	public static byte[] readEncoded(InputStream is, IByteUtil enc) throws IOException{
		final byte[] data = ByteUtil.Base64.getBytes(ByteUtil.UTF8.getString(read(is)));

		is.close();

		return data;
	}
	//encoded Stream end

	//encoded File
	public static void writeEncoded(File f, byte[] data) throws IOException{
		writeEncoded(f, data, DEFAULT_CODER);
	}

	public static void writeEncoded(File f, byte[] data, IByteUtil enc) throws IOException{
		if(data == null)
			throw new IOException(NO_DATA);

		data = ByteUtil.UTF8.getBytes(enc.getString(data));
		write(f, data);
	}

	public static byte[] readEncoded(File f) throws IOException{
		return readEncoded(f, DEFAULT_CODER);
	}

	public static byte[] readEncoded(File f, IByteUtil dec) throws IOException{
		byte[] data = read(f);
		data = dec.getBytes(ByteUtil.UTF8.getString(data));
		return data;
	}
	//encoded File end
	//encoded R/W end

	//mapping
	public static MappedByteBuffer[] getMapping(FileChannel in, FileChannel.MapMode mode) throws IOException {
		return getMapping(in, mode, Integer.MAX_VALUE);
	}

	public static MappedByteBuffer[] getMapping(FileChannel in, FileChannel.MapMode mode, int pSize) throws IOException {
		int pCount = (int)(in.size()/pSize);
		int leftovers = (int)(in.size()%pSize);

		final MappedByteBuffer[] out = new MappedByteBuffer[pCount + (leftovers > 0 ? 1:0)];

		for(int i = 0; i<pCount; i++){
			out[i] = in.map(mode, (long)i*pSize, pSize);
		}

		if(leftovers > 0){
			out[pCount] = in.map(mode, pCount*pSize, leftovers);
		}

		return out;
	}

	//utf conversion

	// convert from UTF-8 -> internal Java String format
	public static String convertFromUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	// convert from internal Java String format -> UTF-8
	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}
	//utf conversion end
}
