package com.crypticbit.ipa.results;

import com.crypticbit.ipa.central.backupfile.BackupFile;

public class FileOnlyLocation extends AbstractLocation
{

	private ContentType contentType;

	public FileOnlyLocation(BackupFile bf, ContentType contentType)
	{
		super(bf);
		this.contentType = contentType;
	}

	@Override
	public ContentType getContentType()
	{
		return contentType;
	}

	@Override
	public String getLocationDescription()
	{
		return getBackupFile().getOriginalFileName();
	}

	@Override
	public String getLocationExtract()
	{
		return null;
	}

}
