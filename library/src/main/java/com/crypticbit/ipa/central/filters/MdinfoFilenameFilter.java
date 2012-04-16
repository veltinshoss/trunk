package com.crypticbit.ipa.central.filters;

import java.io.File;
import java.io.FilenameFilter;

import com.crypticbit.ipa.central.backupfile.Version2BackupFile;


public class MdinfoFilenameFilter implements FilenameFilter
{

	@Override
	public boolean accept(final File dir, final String name)
	{
		return name.endsWith(Version2BackupFile.MDINFO_EXTENSION);
	}
}