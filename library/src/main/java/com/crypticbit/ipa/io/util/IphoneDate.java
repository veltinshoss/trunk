package com.crypticbit.ipa.io.util;

import java.util.Date;

@SuppressWarnings("serial")
public class IphoneDate extends Date
{
	/*
	 * AddressBook.ABPerson.CreationDate firmware 1.1.3 01 - 221372495
	 * 
	 * firmware 2.0 01 - 237680678
	 */

	/**
	 * Seconds from the "java epoch" 1970-01-01 to the "iphone epoch" 2001-01-01
	 */
	static final long EPOCH_2001 = 978307200L;

	/**
	 * Iphone sqlite dbs store the date as seconds since 2001-01-01 00:00 This
	 * method takes in this number and returns a Date object
	 * 
	 * @param secs
	 * @return
	 */
	public static Date iphoneToDate(final long secs)
	{
		// add values together and convert from seconds to milliseconds
		return new Date((EPOCH_2001 + secs) * 1000);
	}

	/**
	 * Used for dynamic proxy
	 * 
	 * @param secs
	 */
	public IphoneDate(final Integer secs)
	{
		this((long) secs);
	}

	public IphoneDate(final long secs)
	{
		super((EPOCH_2001 + secs) * 1000);
	}
	public IphoneDate(final Double secs)
	{
		this(secs.longValue());
	}

	/**
	 * Used for dynamic proxy
	 * 
	 * @param secs
	 */
	public IphoneDate(final String secs)
	{
		this((long) Double.parseDouble(secs));
	}

}
