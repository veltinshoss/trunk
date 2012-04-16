package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

public class SqlMappingException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SqlMappingException(final String message)
	{
		super(message);
	}

	SqlMappingException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
