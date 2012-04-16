/**
 * 
 */
package com.crypticbit.ipa.results;

import com.crypticbit.ipa.central.backupfile.BackupFile;

public class TextLocation implements Location, Comparable<Location>
{
	static final int EXTRACT_PADDING = 10;
	private BackupFile backupFile;
	private String foundString;
	private int start;

	public TextLocation(final ParsedData parsedData,
			final BackupFile backupFile, final String foundString,
			final int start)
	{
		this.foundString = foundString;
		this.backupFile = backupFile;
		this.start = start;
	}

	@Override
	public int compareTo(final Location o)
	{
		if (o instanceof TextLocation)
			return ((Integer) this.start).compareTo(((TextLocation) o).start);
		else
			return getClass().getName().compareTo(o.getClass().getName());
	}

	public BackupFile getBackupFile()
	{
		return this.backupFile;
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.TEXT;
	}

	public int getLength()
	{
		return this.foundString.length();
	}

	@Override
	public String getLocationDescription()
	{
		return this.backupFile.getCompleteOriginalFileName() + " at pos " + this.start;
	}

	public String getLocationExtract()
	{
		return this.foundString;
	}

	/**
	 * @return the start
	 */
	public int getStart()
	{
		return this.start;
	}

	@Override
	public String toString()
	{
		return getLocationDescription() + " - " + getLocationExtract();
	}

}