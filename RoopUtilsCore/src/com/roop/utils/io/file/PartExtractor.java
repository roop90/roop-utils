package com.roop.utils.io.file;

import com.roop.utils.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 09.09.2014
 * Time: 01:57
 * Copyright: roop
 */
public class PartExtractor implements Closeable{
	private final RandomAccessFile ram;
	private final long pSize;
	private int pCount = 0;

	public int partCount(){
		return pCount;
	}
	public int partSize(){
		return (int)pSize;
	}

	public PartExtractor(String file) throws IOException {
		this(file, false);
	}
	public PartExtractor(String file, boolean write) throws IOException {
		this(file, write, Constants.BUFF_L*10);
	}
	public PartExtractor(String file, boolean write, int pSize) throws IOException {
		if(pSize <= 0){
			throw new IllegalArgumentException();
		}

		this.ram = new RandomAccessFile(file, write ? "rw" : "r");
		this.pSize = pSize;
		this.calcPartCount(ram.length(), pSize);
	}
	public PartExtractor(String file, boolean write, int minPsize, int maxPcount) throws IOException {
		if(minPsize <= 0 || maxPcount <= 0){
			throw new IllegalArgumentException();
		}

		this.ram = new RandomAccessFile(file, write ? "rw" : "r");
		this.pSize = (int)(ram.length()/(maxPcount*minPsize)+1)*minPsize;
		this.calcPartCount(ram.length(), pSize);
	}

	private void calcPartCount(long size, long pSize){
		int pCount = (int)(size/pSize);

		if(size%pSize > 0)
			pCount++;

		this.pCount = pCount;
	}

	public long getLength() {
		try {
			return this.ram.length();
		} catch (IOException e) {
			e.printStackTrace();  //TODO: For Debug only
		}
		return -1;
	}

	public void setLength(long l) throws IOException {
		this.ram.setLength(l);
		this.calcPartCount(l, pSize);
	}

	public void fill(FilePart part) throws IOException {
		if(part.getNumber() < 0 || part.getNumber() >= pCount)
			throw new IllegalArgumentException();

		int pSize = part.getNumber() != pCount-1 ? (int)this.pSize : (int)(ram.length() - (pCount-1) * this.pSize);

		byte[] data = new byte[pSize];

		synchronized (ram) {
			ram.seek(part.getNumber() * this.pSize);
			ram.readFully(data);
		}

		part.setData(data);
	}

	public void write(FilePart part) throws IOException {
		if(part.getNumber() < 0)
			throw new IllegalArgumentException();

		synchronized (ram){
			ram.seek(part.getNumber() * pSize);
			ram.write(part.getData());
			this.calcPartCount(ram.length(), pSize);
		}
	}

	@Override
	public void close() throws IOException {
		ram.close();
	}
}
