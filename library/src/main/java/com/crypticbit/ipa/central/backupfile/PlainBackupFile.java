package com.crypticbit.ipa.central.backupfile;

import java.io.File;
import java.io.IOException;

import com.crypticbit.ipa.io.util.IoUtils;


/**
 * This class represents a backup file that has already been restored to disk.
 * It assumes that the filename has been restored.
 * 
 * @author Leo
 * 
 */
public class PlainBackupFile extends BackupFile
{

	protected File contentsFile;

	public PlainBackupFile(final File contentsFile)
	{
		super.originalFileName = contentsFile.getAbsolutePath();
		this.contentsFile = contentsFile;

	}

	@Override
	public File getContentsFile()
	{
		return this.contentsFile;
	}



	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException
	{
		return IoUtils.getBytesFromFile(this.contentsFile);
	}

}
