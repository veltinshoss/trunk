package com.crypticbit.ipa.io.parser.sqlite;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface SqlMetaData
{
	/**
	 * Return the results in a String array representing [row][column]
	 * 
	 * @param table
	 * @param columns
	 * @return
	 * @throws SQLException
	 */
	public String[][] getData(String table, List<String> columns)
			throws SQLException;

	List<String> getColumns(String table) throws SQLException;

	Set<String> getTables() throws SQLException;
}
