/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.io.dynamicproxy.DynamicProxy;
import com.crypticbit.ipa.io.parser.sqlite.SqlDataSource;
import com.crypticbit.ipa.io.parser.sqlite.SqlLocation;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.Where.Expression;
import com.crypticbit.ipa.results.Location;
import com.crypticbit.ipa.util.FormattedArrayList;
import com.sun.rowset.CachedRowSetImpl;

/**
 * This class will map a database table to a Java interface using a dynamic
 * proxy. Methods are associated with fields using the <code>SqlField</code>
 * annotation. the table name is specified using the <code>SqlTable</code>
 * annotation and relationships between tables can be navigated using the
 * <code>SqlRelation</code> annotation. This functionality is entirely read-only
 * and non-transactional.
 * 
 * @author Leo
 * 
 * @param <T>
 *            The type of the interface to be used to get databse data
 */
public class SqlDynamicProxy<T> extends DynamicProxy<T> {
    public static String getFieldName(final Method method)
	    throws SqlMappingException {
	if (method.isAnnotationPresent(SqlField.class))
	    return method.getAnnotation(SqlField.class).value();
	else if (method.isAnnotationPresent(SqlDatabaseKey.class))
	    return "ROWID";
	else
	    return null;
	// throw new SqlMappingException(
	// "There is no SqlField or SqlDatabaseKey annotation present on "
	// + method);
    }

    /**
     * Convenience method for when there are no criteria.
     * 
     * @param <S>
     * @param interfaceDef
     * @param dataSource
     * @return
     * @throws SqlMappingException
     * @throws SQLException
     */
    public static <S> List<S> loadData(final Class<S> interfaceDef,
	    final SqlDataSource dataSource) throws SqlMappingException,
	    SQLException {
	return loadData(interfaceDef, dataSource, Where.NULL_WHERE);
    }

    public static <S> S loadRootData(final Class<S> interfaceDef,
	    final SqlDataSource dataSource) throws SqlMappingException,
	    SQLException {

	// this magic finds "AddressBook.Person" from a class that
	// "extends List<AddressBook.Person>"
	Class typeOfEachElementToBeReturned = (Class) ((ParameterizedType) interfaceDef
		.getGenericInterfaces()[0]).getActualTypeArguments()[0];

	SqlVersionOptions versionAnnotation = (SqlVersionOptions) typeOfEachElementToBeReturned
		.getAnnotation(SqlVersionOptions.class);
	if (versionAnnotation != null) {
	    typeOfEachElementToBeReturned = findMostSuitedVersion(
		    typeOfEachElementToBeReturned, versionAnnotation,
		    dataSource);
	}

	final List data = loadData(typeOfEachElementToBeReturned, dataSource);
	S s = (S) Proxy.newProxyInstance(interfaceDef.getClassLoader(),
		new Class[] { interfaceDef }, new InvocationHandler() {

		    @Override
		    public Object invoke(final Object proxy,
			    final Method method, final Object[] args)
			    throws Throwable {
			// delegate to list
			// This has been removed as it calls problems with
			// autoboxing on primitave args -however it may need to
			// be reintroduced
			// if we start to use this method again for Conceptable
			// Method m =
			// data.getClass().getMethod(method.getName(),
			// asClass(args));
			return method.invoke(data, args);
		    }
		});
	return s;
    }

    // FIXME - This should really be cached - but to do that we need to move to
    // a factory pattern so we can hold state (static would be bad, because we
    // need new state for each file we open
    private static Class findMostSuitedVersion(
	    Class typeOfEachElementToBeReturned,
	    SqlVersionOptions versionAnnotation, SqlDataSource dataSource)
	    throws SqlMappingException, SQLException {
	for (Class clazz : versionAnnotation.value()) {
	    assert (typeOfEachElementToBeReturned.isAssignableFrom(clazz));
	    if (Table.validateTable(clazz, dataSource).isEmpty())
		return clazz;
	    else
		LogFactory
			.getLogger()
			.log(Level.INFO,
				"Not using "
					+ clazz.getName()
					+ " because the following fields are not available: "
					+ Table.validateTable(clazz, dataSource));
	}
	return null;
    }

    private static Class[] asClass(Object[] args) {
	if (args == null)
	    return null;
	Class result[] = new Class[args.length];
	for (int loop = 0; loop < args.length; loop++) {
	    LogFactory.getLogger().log(Level.INFO,
		    args[loop] + "," + args[loop].getClass());
	    result[loop] = args[loop].getClass();
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    // Proxy does not use generics yet
    static <S> List<S> loadData(final Class<S> interfaceDef,
	    final SqlDataSource dataSource, final Where inputWhere)
	    throws SqlMappingException, SQLException {
	Table table = Table.createAndValidateTable(interfaceDef, inputWhere,
		dataSource);
	Connection connection = dataSource.getDbConnection();
	ResultSet rs = table.getResults(connection.createStatement(
		ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
	CachedRowSetImpl crs = new CachedRowSetImpl();
	crs.populate(rs);
	List<S> result = new FormattedArrayList<S>();
	for (int loop = 0; loop < crs.size(); loop++) {
	    result.add((S) Proxy.newProxyInstance(
		    interfaceDef.getClassLoader(),
		    new Class[] { interfaceDef }, new SqlDynamicProxy<S>(
			    interfaceDef, dataSource, crs, loop, table)));
	}
	rs.close();
	return result;
    }

    private SqlDataSource dataSource;

    private int position;

    private ResultSet rs;

    private Table table;

    private SqlDynamicProxy(final Class<T> interfaceDef,
	    final SqlDataSource dataSource, final ResultSet rs,
	    final int position, final Table table) throws SQLException {
	super(interfaceDef);
	this.dataSource = dataSource;
	this.rs = rs;
	this.position = position;
	this.table = table;
    }

    public Object invoke(final Object proxy, final Method m, final Object[] args)
	    throws Throwable {
	this.rs.absolute(this.position + 1);
	// if it is a relation to another table
	if (m.equals(Object.class.getMethod("toString", (Class<?>[]) null)))
	    return dynamicToString(this.interfaceDef, proxy);
	if (m.equals(Object.class.getMethod("equals", Object.class)))
	    return dynamicEquals(args[0]);
	if (m.equals(Object.class.getMethod("hashCode", (Class<?>[]) null)))
	    return dynamicHashCode();
	if (m.getReturnType().equals(Location.class)) {
	    return new SqlLocation(dataSource.getBackupFile(), null,
		    table.getTables(), null, position);
	}
	if (m.isAnnotationPresent(SqlRelation.class)) {
	    SqlRelation annotation = m.getAnnotation(SqlRelation.class);
	    // only find records whose primary key match the current tables
	    // secondary key
	    SimpleWhere criteria = createWhereClauseForNavRelation(m,
		    annotation);
	    if (criteria.isSelectingOnPrimaryKey()) {

		List<?> c = loadData(getUnderlyingReturnType(m),
			this.dataSource, criteria);
		if (c.size() > 0)
		    return DynamicProxy.coerceValueToType(m.getReturnType(),
			    c.get(0));
		else
		    return null;
	    } else {
		return loadData(getUnderlyingReturnType(m), this.dataSource,
			criteria);
	    }
	} else {
	    // get the name of the field
	    String name = getFieldName(m);
	    Object result;
	    if (name == null) {
		result = this;
	    } else {
		result = this.rs.getObject(name);
	    }
	    return DynamicProxy.coerceValueToType(m.getReturnType(), result);
	}
    }

    @Override
    protected boolean isMethodVisible(final Method m) {
	return !m.isAnnotationPresent(SqlDatabaseKey.class);
    }

    /**
     * Creates the where statement equivalent to: <code>fieldName = value</code>
     * where: <code>fieldName</code> is the fieldName related to the
     * <code>method</code> <code>value</code> is the value of the secondary key
     * in the SqlRelation annotation.
     * 
     * @param method
     * @param annotation
     * @return
     * @throws NoSuchMethodException
     * @throws SQLException
     * @throws SqlMappingException
     * @throws SecurityException
     */
    private SimpleWhere createWhereClauseForNavRelation(final Method method,
	    final SqlRelation annotation) throws NoSuchMethodException,
	    SQLException, SecurityException, SqlMappingException {

	Method primaryKeyMethod = getUnderlyingReturnType(method).getMethod(
		annotation.primary(), (Class<?>[]) null);
	Expression left = Where.Expression.createExpression(primaryKeyMethod);
	Expression right = Where.Expression.createExpression(this.rs
		.getObject(getFieldName(method.getDeclaringClass().getMethod(
			annotation.foreign(), (Class<?>[]) null))));
	boolean onlyReturnsOneResult = primaryKeyMethod
		.isAnnotationPresent(SqlDatabaseKey.class);
	SimpleWhere criteria = new SimpleWhere(left, right,
		onlyReturnsOneResult);

	return criteria;
    }

    private boolean dynamicEquals(final Object object) {
	if (!(object instanceof SqlDynamicProxy))
	    return false;
	else
	    return this.table.equals(((SqlDynamicProxy<?>) object).table)
		    && (this.position == ((SqlDynamicProxy<?>) object).position);
    }

    private int dynamicHashCode() {
	return this.table.hashCode(this.position);
    }
}