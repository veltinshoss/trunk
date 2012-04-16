package com.crypticbit.ipa.central;

import com.crypticbit.ipa.central.backupfile.BackupFileException;

public class UnsupportedFileFormat extends BackupFileException
{

	public UnsupportedFileFormat(final String message)
	{
		super(message);
	}

	public UnsupportedFileFormat(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
