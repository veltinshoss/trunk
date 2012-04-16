package com.crypticbit.ipa.central.filters;

import java.io.File;
import java.io.FilenameFilter;

public class NoSuffixFilenameFilter implements FilenameFilter
{

	@Override
	public boolean accept(final File dir, final String name)
	{
		return !name.contains(".");
	}

}
