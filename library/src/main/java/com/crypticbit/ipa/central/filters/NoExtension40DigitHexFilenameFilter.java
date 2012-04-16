package com.crypticbit.ipa.central.filters;

import java.io.File;
import java.io.FilenameFilter;

public class NoExtension40DigitHexFilenameFilter implements FilenameFilter
{
	@Override
	public boolean accept(final File dir, final String name)
	{
		// fixme - ONLY LOOK FOR HEX CHARACTER
		return name.length() == 40;

	}

}
