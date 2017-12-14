package com.roop.utils.io.file;

/**
 * Created with IntelliJ IDEA.
 * User: roop
 * Date: 09.09.2014
 * Time: 02:44
 * Copyright: roop
 */
//todo maybe switch to ByteBuffer and simply refill it
public class FilePart {
	private int pNumber;
	private byte[] data = null;

	public void setData(byte[] data){
		this.data = data;
	}
	public void setNumber(int n){
		pNumber = n;
	}

	public int getNumber(){
		return pNumber;
	}
	public byte[] getData(){
		return data;
	}

	public FilePart(int n){
		this.pNumber = n;
	}

	public FilePart(int n, byte[] d){
		pNumber = n;
		data = d;
	}
}
