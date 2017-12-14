package com.roop.utils.io.download;


import com.roop.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * Project: roop-utils
 * User: roop
 * Date: 18.03.2017
 * Time: 16:00
 * Copyright: Ralf Wiedemann
 */
public class HttpDownload {

	private final URL url;

	public HttpDownload(String url) throws MalformedURLException {
		this(new URL(url));
	}

	public HttpDownload(URL url) {
		this.url = url;
	}

	public int size() throws IOException {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return conn.getContentLength();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public void download(OutputStream out) throws IOException {
		Utils.copy(url.openStream(), out);
	}

	public void download(OutputStream out, Utils.ICopyCallback cb) {
		try {
			Utils.copy(url.openStream(), out, cb);
		} catch (IOException e) {
			cb.onException(e);
		}
	}

	public void download(final OutputStream out, final Utils.ICopyCallback cb, boolean threaded) {
		if(!threaded) {
			this.download(out, cb);
			return;
		}

		Thread t = new Thread(){
			@Override
			public void run() {
				try {
					Utils.copy(url.openStream(), out, cb);
				} catch (IOException e) {
					cb.onException(e);
				}
			}
		};
		t.setDaemon(true);
		t.start();

	}

}
