package com.crypticbit.ipa.io.util;

import java.util.Date;

/**
 * A wrapper for java.util.Date to add more constructors needed by dynamic proxy
 * 
 * @author mat
 */
@SuppressWarnings("serial")
public class PosixDate extends Date
{
	/**
	 * @param secs
	 *            - seconds since the epoch (January 1, 1970, 00:00:00 GMT.)
	 */
	public PosixDate(final Integer secs)
	{
		this((long) secs);
	}

	/**
	 * @param secs
	 *            - seconds since the epoch (January 1, 1970, 00:00:00 GMT.)
	 */
	public PosixDate(final long secs)
	{
		super(secs * 1000);
	}

	/**
	 * @param secs
	 *            - seconds since the epoch (January 1, 1970, 00:00:00 GMT.)
	 */
	public PosixDate(final String secs)
	{
		this((long) Double.parseDouble(secs));
	}
}
