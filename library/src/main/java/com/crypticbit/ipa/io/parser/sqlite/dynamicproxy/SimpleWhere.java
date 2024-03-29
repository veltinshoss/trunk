/**
 * 
 */
package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

class SimpleWhere implements Where
{
	private Expression e1;
	private Expression e2;
	private Where subClause;
	private boolean selectingOnPrimaryKey = false;

	SimpleWhere(final Expression e1, final Expression e2, boolean selectingOnPrimaryKey)
	{
		this.e1 = e1;
		this.e2 = e2;
		this.subClause = Where.NULL_WHERE;
		this.selectingOnPrimaryKey = selectingOnPrimaryKey;
	}

	SimpleWhere(final Expression e1, final Expression e2, final Where subClause)
	{
		this.e1 = e1;
		this.e2 = e2;
		this.subClause = subClause;
	}

	public String formWhereClause() throws SqlMappingException
	{
		return this.e1.getExpressionAsString()
				+ " = "
				+ this.e2.getExpressionAsString()
				+ (this.subClause.needsWhere() ? (" AND " + this.subClause
						.formWhereClause()) : "");
	}

	@Override
	public boolean needsWhere()
	{
		return true;
	}
	
	public boolean isSelectingOnPrimaryKey() {
	    return selectingOnPrimaryKey;
	}

}