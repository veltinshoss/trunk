package monkeypuzzle.io.parser.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlDataSource implements SqlMetaData
{

	static
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e)
		{
			throw new Error("The org.sqlite.JDBC has not been found - dying");
		}
	}

	private File dataFile;
	protected Connection dbConnection;

	public SqlDataSource(final File dataFile) throws SQLException, IOException
	{
		this.dataFile = dataFile;
		this.dbConnection = DriverManager.getConnection("jdbc:sqlite:"
				+ dataFile.getAbsolutePath());
	}

	@Override
	public List<String> getColumns(final String table) throws SQLException
	{
		List<String> results = new ArrayList<String>();
		DatabaseMetaData metadata = getDbConnection().getMetaData();
		ResultSet columnNames = metadata.getColumns(null, "%", table, "%");
		while (columnNames.next())
		{
			results.add(columnNames.getString("COLUMN_NAME"));
		}
		return results;
	}

	@Override
	public String[][] getData(final String table, final List<String> columns)
			throws SQLException
	{
		String query = "select "
				+ columns.toString().substring(1,
						columns.toString().length() - 1) + " from \"" + table
				+ "\"";
		try
		{
			List<String[]> results = new ArrayList<String[]>();
			Statement statement = getDbConnection().createStatement();
			statement.execute(query);
			ResultSet rs = statement.getResultSet();
			while (rs.next())
			{
				String[] line = new String[columns.size()];
				for (int i = 0; i < columns.size(); i++)
				{
					line[i] = rs.getString(i + 1);
				}
				results.add(line);
			}
			return results.toArray(new String[results.size()][]);
		} catch (SQLException s)
		{
			throw new SQLException("trying to execute " + query, s);
		}
	}

	public File getDataFile()
	{
		return this.dataFile;
	}

	public Connection getDbConnection()
	{
		return this.dbConnection;
	}

	@Override
	public List<String> getTables() throws SQLException
	{
		List<String> results = new ArrayList<String>();
		DatabaseMetaData metadata = getDbConnection().getMetaData();
		ResultSet tableNames = metadata.getTables(null, "%", "%",
				new String[] { "TABLE" });
		while (tableNames.next())
		{
			results.add(tableNames.getString("TABLE_NAME"));
		}
		return results;
	}

	@Override
	public String toString()
	{
		try
		{
			return "Database comprising tables: " + getTables();
		} catch (SQLException e)
		{
			return "Database that can not be examined because of "
					+ e.getLocalizedMessage();
		}
	}

}
