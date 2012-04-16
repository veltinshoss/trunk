package com.crypticbit.ipa.io.parser;

import com.crypticbit.ipa.central.FileParseException;

public class BadFileFormatException extends FileParseException
{

	public BadFileFormatException(String string, Exception e)
	{
		super(string,e);
	}

}
