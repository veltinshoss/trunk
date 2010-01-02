/**
 * 
 */
package monkeypuzzle.io.parser.sqlite.dynamicproxy;

import java.lang.reflect.Method;

interface Where
{
	abstract static class Expression
	{
		static Where.Expression createExpression(final Method method)
		{
			return new Expression() {
				@Override
				String getExpressionAsString() throws SqlMappingException
				{
					return (method.getDeclaringClass()
							.getAnnotation(SqlTable.class)).tableName()
							+ "." + SqlDynamicProxy.getFieldName(method);
				}
			};
		}

		static Where.Expression createExpression(final Object value)
		{
			return new Expression() {

				@Override
				String getExpressionAsString()
				{
					return "'" + value + "'";
				}
			};
		}

		abstract String getExpressionAsString() throws SqlMappingException;
	}

	final Where NULL_WHERE = new Where() {

		@Override
		public String formWhereClause()
		{
			return null;
		}

		@Override
		public boolean needsWhere()
		{
			return false;
		}
	};

	String formWhereClause() throws SqlMappingException;

	boolean needsWhere();

}