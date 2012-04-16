
package com.crypticbit.ipa.central.backupfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.crypticbit.ipa.io.util.IoUtils;


public class Version4BackupFile extends BackupFile
{
	public static final String UNKNOWN_FAKE_PATH = " UNKNOWN/";
	private File contentsFile;

	public Version4BackupFile(final String originalName, final File file)
			throws FileNotFoundException, IOException
	{
		this.contentsFile = file;
		this.originalFileName = originalName;
		
	}

	@Override
	public File getContentsFile() throws IOException
	{
		return this.contentsFile;
	}



	@Override
	protected byte[] createByteArrayFromBackupFile() throws IOException,
			FileNotFoundException
	{
		return IoUtils.getBytesFromFile(this.contentsFile);
	}

	

}
