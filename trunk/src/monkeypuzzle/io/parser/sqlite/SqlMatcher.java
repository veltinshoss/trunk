package monkeypuzzle.io.parser.sqlite;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;
import monkeypuzzle.results.MatcherException;
import monkeypuzzle.util.RegEx;

public class SqlMatcher implements Matcher, Comparable<SqlMatcher>
{
	public static final String ELEMENT_SEP = ":";

	/**
	 * Takes a string in the form <code>table:column:row</code> where
	 * <ul>
	 * <li>table - is a table name. * and ? can be used as wildcards
	 * <li>column is a column name. * and omitted both match everything
	 * <li>row is a row number. * and omitted match everything
	 * </ul>
	 * 
	 * @param parseable
	 * @return
	 * @throws MatcherException
	 */
	public static Matcher parseMatcher(final String parseable)
			throws MatcherException
	{
		String table;
		String columnName = null;
		int row = -1;
		final String[] matchComponent = parseable.split(ELEMENT_SEP);
		if (matchComponent.length >= 1)
		{
			table = matchComponent[0];
		} else
			throw new MatcherException("Not enough elements in: " + parseable);
		if ((matchComponent.length >= 2) && !matchComponent[1].equals("*"))
		{
			columnName = matchComponent[1];
		}
		try
		{
			if ((matchComponent.length >= 3) && !matchComponent[1].equals("*")
					&& !matchComponent[1].equals("#"))
			{
				row = Integer.parseInt(matchComponent[2]);
			}
		} catch (NumberFormatException nfe)
		{
			throw new MatcherException("Row index is not a number in "
					+ matchComponent[2], nfe);
		}
		return new SqlMatcher(table, columnName, row);
	}

	protected String table;
	protected String columnName;

	protected int row;

	SqlMatcher(final String table, final String columnName, final int row)
	{
		this.table = table;
		this.columnName = columnName;
		this.row = row;
	}

	@Override
	public int compareTo(final SqlMatcher o)
	{
		if (!this.table.equals(o.table))
			return this.table.compareTo(o.table);
		if (!((this.columnName == null) || this.columnName.equals(o.columnName)))
			return this.columnName.compareTo(o.columnName);
		return this.row - o.row;
	}

	@Override
	public Set<Location> match(final BackupFile bfd, final Object objectToMatch)
	{
		if ((objectToMatch instanceof SqlMetaData))
		{
			try
			{
				SqlMetaData md = (SqlMetaData) objectToMatch;
				String encodedTableName = RegEx.defaultRegEx.encode(this.table);
				Set<Location> result = new HashSet<Location>();
				for (String thisTable : md.getTables())
				{
					if (thisTable.matches(encodedTableName))
					{
						result.addAll(dealAllColumns(bfd, md, thisTable));
					}
				}
				return result;
			} catch (SQLException se)
			{
				se.printStackTrace();
				return null;
				// FIXME
			}
		}
		return Collections.emptySet();
	}

	@Override
	public String toString()
	{
		return this.table + "." + this.columnName
				+ (this.row == -1 ? "" : " (row " + this.row + ")");
	}

	private Location createLocation(final BackupFile bfd,
			final String tableName, final String thisColumn,
			final String[][] data, final int i)
	{
		return new SqlLocation(bfd, data[i][0], tableName, thisColumn, i);
	}

	private Set<Location> dealAllColumns(final BackupFile bfd,
			final SqlMetaData md, final String thisTable) throws SQLException
	{
		if (this.columnName == null)
		{
			Set<Location> result = new HashSet<Location>();
			for (String column : md.getColumns(thisTable))
			{
				result.addAll(processEachColumn(bfd, md, thisTable, column));
			}
			return result;
		} else if (md.getColumns(thisTable).contains(this.columnName))
			return processEachColumn(bfd, md, thisTable, this.columnName);
		else
			return Collections.emptySet();
	}

	private Set<Location> processEachColumn(final BackupFile bfd,
			final SqlMetaData md, final String tableName,
			final String columnName) throws SQLException
	{
		Set<Location> result = new HashSet<Location>();
		String[][] data = md.getData(tableName, Arrays
				.asList(new String[] { columnName }));
		if (this.row == -1)
		{
			for (int i = 0; i < data.length; i++)
			{
				result.add(createLocation(bfd, tableName, columnName, data, i));
			}
		} else if (this.row < data.length)
		{
			result.add(createLocation(bfd, tableName, columnName, data,
					this.row));
		}
		return result;
	}
}