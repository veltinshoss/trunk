/**
 * 
 */
package monkeypuzzle.io.parser.sqlite;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import monkeypuzzle.central.FileParseException;
import monkeypuzzle.central.NavigateException;
import monkeypuzzle.central.backupfile.BackupFile;
import monkeypuzzle.io.parser.sqlite.dynamicproxy.SqlDynamicProxy;
import monkeypuzzle.results.Location;
import monkeypuzzle.results.Matcher;
import monkeypuzzle.results.ParsedDataImpl;
import monkeypuzzle.results.TextSearchAlgorithm;

public class SqlResultsImpl extends ParsedDataImpl implements
		monkeypuzzle.results.ParsedData, SqlResults
{
	private BackupFile bfd;
	private SqlDataSource sqlDataSource;

	/**
	 * @param sqlParser
	 */
	SqlResultsImpl(final BackupFile bfd, final SqlDataSource sqlDataSource)
	{
		this.bfd = bfd;
		this.sqlDataSource = sqlDataSource;
	}

	@Override
	public BackupFile getBackupFile()
	{
		return this.bfd;
	}

	@Override
	public <I> I getContentbyInterface(final Class<I> interfaceDef)
	{
		try
		{
			return SqlDynamicProxy.loadRootData(interfaceDef,
					this.sqlDataSource);
		} catch (Exception e)
		{
			// FIXME error handling
			throw new Error("Unable to load data into type \""
					+ interfaceDef.getName() + "\"", e);
		}
	}

	public String getContents()
	{
		StringBuffer buff = new StringBuffer();
		for (Class<?> interfaceDef : getAvailableInterfaces())
		{
			buff.append(
					"== "
							+ monkeypuzzle.util.StringTools
									.getClassNameNoPackage(interfaceDef)
							+ " Entries ==").append(
					monkeypuzzle.io.util.Util.SYSTEM_LINE_SEPARATOR);
			try
			{
				for (Object p : getRecords(interfaceDef))
				{
					buff.append("= Entry =").append(
							monkeypuzzle.io.util.Util.SYSTEM_LINE_SEPARATOR);
					buff.append(p.toString());
					buff
							.append(monkeypuzzle.io.util.Util.SYSTEM_LINE_SEPARATOR);
					buff
							.append(monkeypuzzle.io.util.Util.SYSTEM_LINE_SEPARATOR);
				}
			} catch (FileParseException e)
			{
				return "Error getting " + interfaceDef.getName() + " Entries: "
						+ e.getMessage();
			}
		}
		return buff.toString();
	}

	public SqlMetaData getMetaData()
	{
		return this.sqlDataSource;
	}

	public <T> List<T> getRecords(final Class<T> interfaceDef)
			throws FileParseException
	{
		try
		{
			return SqlDynamicProxy.loadData(interfaceDef, this.sqlDataSource);
		} catch (Exception e)
		{
			throw new FileParseException("Unable to load data into type +\""
					+ interfaceDef.getName() + "\"", e);
		}
	}

	public String getSummary()
	{
		StringBuffer result = new StringBuffer();
		for (Class<?> interfaceDef : getAvailableInterfaces())
		{
			try
			{
				result.append(getRecords(interfaceDef).size()
						+ " entries of type " + interfaceDef.getSimpleName()
						+ ". ");
			} catch (FileParseException e)
			{
				result.append("<error parsing data for \"" + interfaceDef
						+ "\">");
			}
		}
		return result.toString();
	}

	@Override
	public Set<Location> match(final Matcher matcher)
	{
		return matcher.match(this.bfd, getMetaData());
	}

	@Override
	public Set<Location> search(final TextSearchAlgorithm searchType,
			final String searchString) throws NavigateException
	{
		Set<Location> result = new HashSet<Location>();
		try
		{
			for (String table : this.sqlDataSource.getTables())
			{
				String[][] data = this.sqlDataSource.getData(table,
						this.sqlDataSource.getColumns(table));
				for (int x = 0; x < data.length; x++)
				{
					for (int y = 0; y < data[x].length; y++)
						if ((data[x][y] != null)
								&& (searchType.search(searchString, data[x][y])
										.size() > 0))
						{
							result.add(new SqlLocation(this.bfd, data[x][y],
									table, this.sqlDataSource.getColumns(table)
											.get(y), x));
						}
				}
			}
		} catch (SQLException e)
		{
			throw new NavigateException("unable to access SQL element", e);
		}
		return result;
	}

	@Override
	public String toString()
	{
		return getContents();
	}
}