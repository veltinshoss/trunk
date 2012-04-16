/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite;

import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.results.AbstractLocation;
import com.crypticbit.ipa.results.ContentType;
import com.crypticbit.ipa.results.Location;

public class SqlLocation extends AbstractLocation
{
	private String value;
	private String table;
	private String columnName;
	private int row;

	/**
	 * 
	 * @param bfd
	 * @param value
	 * @param table
	 * @param columnName
	 *            the sql column name, where null represents unspecified
	 * @param row
	 *            the row where -1 represents unspecified
	 */
	public SqlLocation(final BackupFile bfd, final String value,
			final String table, final String columnName, final int row)
	{
		super(bfd);
		this.value = value;

		this.table = table;
		this.row = row;
		this.columnName = columnName;
	}

	@Override
	public int compareTo(final Location location)
	{
		if (!(location instanceof SqlLocation))
			return this.getClass().getName()
					.compareTo(location.getClass().getName());
		SqlLocation loc = (SqlLocation) location;
		if (this.getBackupFile() != loc.getBackupFile())
			return this.getBackupFile().compareTo(loc.getBackupFile());
		else
			return getLocationDescription().compareTo(
					loc.getLocationDescription());
	}


	public String getColumn()
	{
		return columnName;
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.SQL;
	}

	@Override
	public String getLocationDescription()
	{
		return getBackupFile() + ": " + table + ":" + columnName + ":" + row;
	}

	public String getLocationExtract()
	{
		return this.value;
	}

	public int getRow()
	{
		return row;
	}

	public String getTable()
	{
		return table;
	}

}