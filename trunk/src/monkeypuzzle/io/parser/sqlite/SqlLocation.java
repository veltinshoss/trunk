/**
 * 
 */
package monkeypuzzle.io.parser.sqlite;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.AbstractLocation;
import monkeypuzzle.results.ContentType;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.LocationMatcher;
import monkeypuzzle.results.Matcher;

public class SqlLocation extends AbstractLocation
{
	private BackupFile bfd;
	private String value;
	private SqlMatcher matcher;

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
		this.matcher = new SqlMatcher(table, columnName, row);
		this.bfd = bfd;
		this.value = value;
	}

	@Override
	public int compareTo(final Location location)
	{
		if (!(location instanceof SqlLocation))
			return this.getClass().getName().compareTo(
					location.getClass().getName());
		SqlLocation loc = (SqlLocation) location;
		if (this.bfd != loc.bfd)
			return this.bfd.compareTo(loc.bfd);
		else
			return this.matcher.compareTo(loc.matcher);
	}

	public BackupFile getBackupFile()
	{
		return this.bfd;
	}

	public String getColumn()
	{
		return this.matcher.columnName;
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.SQL;
	}

	@Override
	public String getLocationDescription()
	{
		return this.bfd + ": " + this.matcher.toString();
	}

	public String getLocationExtract()
	{
		return this.value;
	}

	@Override
	public LocationMatcher getLocationMatcher()
	{
		return AbstractLocation.createLocationMatcher(getBackupFile(), this);
	}

	@Override
	public Matcher getMatcher()
	{
		return this.matcher;
	}

	public int getRow()
	{
		return this.matcher.row;
	}

	public String getTable()
	{
		return this.matcher.table;
	}

}