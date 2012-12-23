/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crypticbit.ipa.io.parser.sqlite.SqlDataSource;

abstract class Table {

    protected Set<String> tables;
    protected Where where;

    public static class JoinTable extends Table {
	private static <S> Where.Expression createPartOfWhereClause(
		final int index, final Class<S> interfaceDef,
		final SqlJoin join, final List<SqlTable> tableAnnotations)
		throws SqlMappingException {
	    try {
		return Where.Expression.createExpression(interfaceDef
			.getInterfaces()[index].getMethod(
			index == 0 ? join.firstKey() : join.secondKey(),
			(Class<?>[]) null));
	    } catch (SecurityException e) {
		throw new SqlMappingException(
			"The method specified by the annotation could not be accessed",
			e);
	    } catch (NoSuchMethodException e) {
		throw new SqlMappingException(
			"The method specified by the annotation could not be found",
			e);
	    }
	}

	JoinTable(final Class<?> interfaceDef, final Where inputWhere,
		SqlDataSource dataSource) throws SqlMappingException,
		SQLException {
	    final SqlJoin join = interfaceDef.getAnnotation(SqlJoin.class);
	    List<SqlTable> tableAnnotations = getTableAnnotations(interfaceDef);
	    this.tables = getTableNamesFromJoin(tableAnnotations, dataSource);
	    this.where = new SimpleWhere(createPartOfWhereClause(0,
		    interfaceDef, join, tableAnnotations),
		    createPartOfWhereClause(1, interfaceDef, join,
			    tableAnnotations), inputWhere);
	}

	private static List<SqlTable> getTableAnnotations(
		final Class<?> interfaceDef) {
	    List<SqlTable> tableAnnotations = new ArrayList<SqlTable>();
	    for (Class<?> interface0 : interfaceDef.getInterfaces()) {
		if (interface0.isAnnotationPresent(SqlTable.class))
		tableAnnotations
			.add((interface0.getAnnotation(SqlTable.class)));
	    }
	    return tableAnnotations;
	}
    }

    private static Set<String> getTableNamesFromJoin(
	    Collection<SqlTable> sqlTables, SqlDataSource dataSource)
	    throws SqlMappingException {
	Set<String> tables = new HashSet<String>();
	for (SqlTable table : sqlTables) {
	    tables.add(table.tableName());
	}
	return tables;
    }

    public static class SimpleTable extends Table {
	SimpleTable(final Class<?> interfaceDef, final Where inputWhere,
		SqlDataSource dataSource) throws SqlMappingException,
		SQLException {
	    SqlTable table = interfaceDef.getAnnotation(SqlTable.class);
	    this.tables = new HashSet<String>();
	    this.tables.add(getTableName(table, dataSource));
	    this.where = inputWhere;

	}
    }

    static Table createAndValidateTable(final Class<?> interfaceDef,
	    final Where where, SqlDataSource dataSource)
	    throws SqlMappingException, SQLException {

	if (interfaceDef.isAnnotationPresent(SqlValidateFieldsPresent.class)) {
	    Set<String> missingColumns = validateTable(interfaceDef, dataSource);
	    if (!missingColumns.isEmpty())
		throw new SqlMappingException(interfaceDef + " didn't provide "
			+ missingColumns.toString() + " columns");
	}
	return createTable(interfaceDef, where, dataSource);

    }

    static Table createTable(final Class<?> interfaceDef, final Where where,
	    SqlDataSource dataSource) throws SqlMappingException, SQLException {

	if (interfaceDef.isAnnotationPresent(SqlTable.class))
	    return new SimpleTable(interfaceDef, where, dataSource);
	else if (interfaceDef.isAnnotationPresent(SqlJoin.class))
	    return new JoinTable(interfaceDef, where, dataSource);
	else
	    throw new SqlMappingException("No annotation present on "
		    + interfaceDef);

    }

    static Set<String> validateTable(final Class<?> interfaceDef,
	    SqlDataSource dataSource) throws SqlMappingException, SQLException {

	Set<String> tables;
	if (interfaceDef.isAnnotationPresent(SqlTable.class)) {
	    tables = getTableNamesFromJoin(
		    JoinTable.getTableAnnotations(interfaceDef), dataSource);
	} else if (interfaceDef.isAnnotationPresent(SqlJoin.class)) {
	    tables = new HashSet<String>();
	    tables.add(getTableName(interfaceDef.getAnnotation(SqlTable.class),
		    dataSource));
	} else
	    throw new SqlMappingException("\"" + interfaceDef
		    + "\" isn't Sql annotated");

	Set<String> neededColumns = new HashSet<String>();
	if (interfaceDef.getAnnotation(SqlValidateFieldsPresent.class) != null) {
	    for (Method m : interfaceDef.getMethods()) {
		String columnName = SqlDynamicProxy.getFieldName(m);
		if (columnName != null)
		    neededColumns.add(columnName);
	    }
	}
	return dataSource.validate(tables, neededColumns);

    }

    /**
     * If a single tableName is specified then return that, if not check each of
     * the others in turn until you find a valid one
     */
    public static String getTableName(SqlTable sqlTable,
	    SqlDataSource dataSource) throws SqlMappingException {
	if (sqlTable.tableName() != null && sqlTable.tableName().length() > 0)
	    return sqlTable.tableName();
	else {
	    try {
		for (String tableName : sqlTable.tableNames()) {
		    if (dataSource.containsTable(tableName)) {
			return tableName;
		    }
		}
	    } catch (SQLException e) {
		throw new SqlMappingException(
			"Unable to get table names to check");
	    }
	}
	throw new SqlMappingException(
		"None of the specified tables name could be found: "
			+ sqlTable.tableNames());

    }

    /**
     * Return a hashcode for a given record inside the results created by this
     * table
     * 
     * @param position
     *            the record to use to create the hashcode
     * @return a hashcode created using enough details of the query to make it
     *         specific to the query and the record position in the result set
     */
    public int hashCode(final int position) {
	return (getTables() + getWhere() + position).hashCode();
    }

    protected String getTables() {

	StringBuilder buff = new StringBuilder();
	String sep = "";
	for (String str : this.tables) {
	    buff.append(sep);
	    buff.append(str);
	    sep = ",";
	}
	return buff.toString();
    }

    protected Where getWhere() {
	return this.where;
    }

    ResultSet getResults(final Statement statement) throws SQLException,
	    SqlMappingException {
	return statement.executeQuery("select * from "
		+ getTables()
		+ " "
		+ (getWhere().needsWhere() ? " WHERE "
			+ getWhere().formWhereClause() : ""));
    }
}