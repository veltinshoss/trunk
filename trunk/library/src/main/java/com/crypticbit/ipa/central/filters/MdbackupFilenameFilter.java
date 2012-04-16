package com.crypticbit.ipa.central.filters;

import java.io.File;
import java.io.FilenameFilter;

public class MdbackupFilenameFilter implements FilenameFilter
{
	@Override
	public boolean accept(final File dir, final String name)
	{
		// loosely check for 40 char hash
		if (name.indexOf('.') == 40)
			return name.endsWith(".mdbackup");
		return false;
	}

}
