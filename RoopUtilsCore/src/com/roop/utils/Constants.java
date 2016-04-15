package com.roop.utils;

import com.roop.utils.exception.handling.IExHandler;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 17.04.2014
 * Time: 19:07
 * Copyright: roop
 */
public final class Constants {
	private Constants(){}

	public final static boolean isAndroid = System.getProperty("java.vendor").equalsIgnoreCase("The Android Project");

	public final static int BUFF_S = 64*1024;
	public final static int BUFF_M = 256*1024;
	public final static int BUFF_L = 1024*1024;

	public final static IExHandler EXH = new IExHandler() {
		@Override
		public void exceptionCaught(Throwable e, Object source) {
			System.out.println(e + " in " + source);
		}
	};

	/*
	public final static IRemoteInit INIT = new IRemoteInit() {
		@Override
		public OutputStream getOutputStream(OutputStream out) throws IOException {
			return new BufferedOutputStream(out, BUFF_S);
		}

		@Override
		public InputStream getInputStream(InputStream in) throws IOException {
			return in;
		}
	};
	public final static IRemoteInit GZIP_INIT = new IRemoteInit() {
		@Override
		public OutputStream getOutputStream(OutputStream outputStream) throws IOException {
			return new MyGZIPOutputStream(new BufferedOutputStream(outputStream, Constants.BUFF_S), true);
		}

		@Override
		public InputStream getInputStream(InputStream inputStream) throws IOException {
			return new GZIPInputStream(inputStream, Constants.BUFF_S);
		}
	};
	public final static IExceptionHandler EXH = new IExceptionHandler() {
		@Override
		public void exceptionCaught(Exception e, Object source) {
			System.out.println(e + " in " + source);
		}
	};

	public final static Packet BEAT_PACKET = new Packet(Integer.MIN_VALUE);
	*/
}
