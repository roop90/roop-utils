package com.roop.utils.binary;

import java.io.UnsupportedEncodingException;

public enum ByteUtil implements IByteUtil {
	Hex{
		
		@Override
		public String getString(byte[] data) {
			return data != null ? javax.xml.bind.DatatypeConverter.printHexBinary(data).toLowerCase() : null;
		}

		@Override
		public byte[] getBytes(String s) {
			return s != null ? javax.xml.bind.DatatypeConverter.parseHexBinary(s) : null;
		}
		
	},
	Base64{

		@Override
		public String getString(byte[] data) {
			return data != null ? javax.xml.bind.DatatypeConverter.printBase64Binary(data) : null;
		}

		@Override
		public byte[] getBytes(String s) {
			return s != null ? javax.xml.bind.DatatypeConverter.parseBase64Binary(s) : null;
		}
		
	},
	UTF8{

		@Override
		public String getString(byte[] data) {
			try {
				return new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}

		@Override
		public byte[] getBytes(String s) {
			try {
				return s.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		
	}
}
