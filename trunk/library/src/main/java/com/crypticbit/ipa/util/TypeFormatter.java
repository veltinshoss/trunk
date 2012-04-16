package com.crypticbit.ipa.util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;

public final class TypeFormatter
{
	public static String formatTypeAsString(final Object object)
	{
		if (object == null)
			return "null";
		if (object instanceof Date)
			return DateFormat.getInstance().format(object);
		if (object instanceof byte[])
			return toHex((byte[]) object);
		return object.toString();
	}

	public static String toHex(final byte[] bytes)
	{
		if (bytes.length == 0)
			return "";
		final int length = 24;
		boolean tooLong = bytes.length > length;
		// Create a BigInteger using the byte array
		BigInteger bi = new BigInteger(tooLong ? truncate(bytes, length)
				: bytes);
		// Format to hexadecimal
		String s = bi.toString(16);
		if (s.length() % 2 != 0)
		{
			// Pad with 0
			s = "0" + s;
		}
		if (tooLong)
		{
			s = s + "...";
		}
		return s;

	}

	private static byte[] truncate(final byte[] b1, final int length)
	{
		byte[] b2 = new byte[length];
		System.arraycopy(b1, 0, b2, 0, length);
		return b2;
	}

	private TypeFormatter()
	{
	}

}
