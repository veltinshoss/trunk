/**
 * 
 */
package com.crypticbit.ipa.results;

class OrderedString implements Comparable<OrderedString>
{
	private int i;
	private String string;

	public OrderedString(final String string, final int i)
	{
		this.string = string;
		this.i = i;
	}

	@Override
	public int compareTo(final OrderedString o)
	{
		return ((Integer) this.i).compareTo(o.i);
	}

	@Override
	public String toString()
	{
		return this.string;
	}

}