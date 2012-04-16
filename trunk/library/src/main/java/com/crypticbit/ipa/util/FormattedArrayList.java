/**
 * 
 */
package com.crypticbit.ipa.util;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class FormattedArrayList<S> extends ArrayList<S>
{
	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		boolean first = true;
		for (S entry : this)
		{
			if (first)
			{
				first = false;
			} else
			{
				buf.append(",");
			}
			buf.append("\n\t" + entry.toString().replaceAll("\n", "\n\t"));
		}
		buf.append("]");
		return buf.toString();
	}

}