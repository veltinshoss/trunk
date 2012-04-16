package com.crypticbit.ipa.io.parser.manifest;

import java.io.IOException;

public abstract class DataSource implements ByteSource {
	int offset = 0;

	public int getNext() throws IOException {
		offset++;
		return getNext_();
	}

	protected abstract int getNext_() throws IOException;

	public long readUnsignedInt(int numberBytes) throws IOException {
		long total = 0;
		for (int loop = 0; loop < numberBytes; loop++) {
			total = total << 8;
			total = total | (0x000000FF & ((int) getNext()));
		}

		return total;
	}

	public byte[] readByteArray(int length) throws IOException {
		byte result[] = new byte[length];
		for (int loop = 0; loop < length; loop++) {
			result[loop] = (byte) getNext();
		}
		return result;
	}

	public char[] readCharArray(int length) throws IOException {
		char result[] = new char[length];
		for (int loop = 0; loop < length; loop++) {
			result[loop] = (char) getNext();
		}
		return result;
	}

	public String readHexCharArray(int length) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (char c : readCharArray(length)) {
			buf.append(pad(Integer.toHexString((int) c)));
		}
		return buf.toString();

	}

	private String pad(String hexString) {
		if (hexString.length() == 2)
			return hexString;
		if (hexString.length() == 1)
			return "0" + hexString;
		else
			return "00";
	}

	public String readString() throws IOException {

		int length = (int) readUnsignedInt(2);
		if (length == 65535)
			return null;
		String s = new String(readByteArray(length), "UTF8");
		return s;
	}

	public int getCurrentOffset() {
		return offset;
	}

}
