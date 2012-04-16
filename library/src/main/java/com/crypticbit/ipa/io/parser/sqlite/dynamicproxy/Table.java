/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.crypticbit.ipa.io.parser.sqlite.SqlDataSource;

abstract class Table
{
	public static class JoinTable extends Table
	{
		private static <S> Where.Expression createPartOfWhereClause(
				final int index, final Class<S> interfaceDef,
				final SqlJoin join, final List<SqlTable> tableAnnotations)
				throws SqlMappingException
		{
			try
			{
				return Where.Expression.createExpression(interfaceDef
						.getInterfaces()[index].getMethod(
						index == 0 ? join.firstKey() : join.secondKey(),
						(Class<?>[]) null));
			} catch (SecurityException e)
			{
				throw new SqlMappingException(
						"The method specified by the annotation could not be accessed",
						e);
			} catch (NoSuchMethodException e)
			{
				throw new SqlMappingException(
						"The method specified by the annotation could not be found",
						e);
			}
		}

		JoinTable(final Class<?> interfaceDef, final Where inputWhere,
				SqlDataSource dataSource) throws SqlMappingException
		{
			final SqlJoin join = interfaceDef.getAnnotation(SqlJoin.class);
			List<SqlTable> tableAnnotations = new ArrayList<SqlTable>();
			for (Class<?> interface0 : interfaceDef.getInterfaces())
			{
				tableAnnotations
						.add((interface0.getAnnotation(SqlTable.class)));
			}
			this.tables = getTableName(tableAnnotations.get(0), dataSource)
					+ "," + getTableName(tableAnnotations.get(1), dataSource);
			this.where = new SimpleWhere(createPartOfWhereClause(0,
					interfaceDef, join, tableAnnotations),
					createPartOfWhereClause(1, interfaceDef, join,
							tableAnnotations), inputWhere);
		}
	}

	public static class SimpleTable extends Table
	{
		SimpleTable(final Class<?> interfaceDef, final Where inputWhere,
				SqlDataSource dataSource) throws SqlMappingException
		{
			SqlTable table = interfaceDef.getAnnotation(SqlTable.class);
			this.tables = getTableName(table, dataSource);
			this.where = inputWhere;
		}
	}

	static Table createTable(final Class<?> interfaceDef, final Where where,
			SqlDataSource dataSource) throws SqlMappingException
	{
		if (interfaceDef.isAnnotationPresent(SqlTable.class))
			return new SimpleTable(interfaceDef, where, dataSource);
		else if (interfaceDef.isAnnotationPresent(SqlJoin.class))
			return new JoinTable(interfaceDef, where, dataSource);
		else
			throw new SqlMappingException("No annotation present on "
					+ interfaceDef);
	}

	/**
	 * If a single tableName is specified then return that, if not check each of
	 * the others in turn until you find a valid one
	 */
	public String getTableName(SqlTable sqlTable, SqlDataSource dataSource)
			throws SqlMappingException
	{
		if (sqlTable.tableName() != null && sqlTable.tableName().length() > 0)
			return sqlTable.tableName();
		else
		{
			try
			{
				for (String tableName : sqlTable.tableNames())
				{
					if (dataSource.containsTable(tableName))
					{
						return tableName;
					}
				}
			} catch (SQLException e)
			{
				throw new SqlMappingException(
						"Unable to get table names to check");
			}
		}
		throw new SqlMappingException(
				"None of the specified tables name coudl be found: "
						+ sqlTable.tableNames());

	}

	protected String tables;

	protected Where where;

	/**
	 * Return a hashcode for a give record inside the results created by this
	 * table
	 * 
	 * @param position
	 *            the record to use to create the hashcode
	 * @return a hascode created using enough details of the query to make it
	 *         specfic to teh query and the record position in the result set
	 */
	public int hashCode(final int position)
	{
		return (getTables() + getWhere() + position).hashCode();
	}

	protected String getTables()
	{
		return this.tables;
	}

	protected Where getWhere()
	{
		return this.where;
	}

	ResultSet getResults(final Statement statement) throws SQLException,
			SqlMappingException
	{
		return statement.executeQuery("select * from "
				+ getTables()
				+ " "
				+ (getWhere().needsWhere() ? " WHERE "
						+ getWhere().formWhereClause() : ""));
	}
}