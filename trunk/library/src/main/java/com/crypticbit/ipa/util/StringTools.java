package com.crypticbit.ipa.util;

public class StringTools
{

	public static final String getClassNameNoPackage(final Class c)
	{
		String name = c.getName();
		int nameStart = name.lastIndexOf('.');
		return name.substring(nameStart);
	}

}
