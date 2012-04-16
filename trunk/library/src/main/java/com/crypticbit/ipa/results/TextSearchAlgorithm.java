/**
 * 
 */
package com.crypticbit.ipa.results;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextSearchAlgorithm
{

	CASE_INSENSITIVE()
	{
		@Override
		public Map<Integer, String> search(final String searchFor,
				final String searchWithin)
		{
			return CASE_SENSITIVE.search(searchFor.toLowerCase(), searchWithin
					.toLowerCase());
		}
	},
	CASE_SENSITIVE()
	{
		@Override
		public Map<Integer, String> search(final String searchFor,
				final String searchWithin)
		{
			Map<Integer, String> result = new TreeMap<Integer, String>();
			int start = -1;
			while ((start = searchWithin.indexOf(searchFor, start + 1)) >= 0)
			{
				result.put(start, searchFor);
			}
			return result;
		}
	},
	REGULAR_EXPRESSION()
	{
		@Override
		public Map<Integer, String> search(final String searchFor,
				final String searchWithin)
		{
			Map<Integer, String> result = new TreeMap<Integer, String>();
			Pattern p = Pattern.compile(searchFor);
			Matcher m = p.matcher(searchWithin);
			while (m.find())
			{
				result.put(m.start(), m.group());
			}
			return result;
		}
	},
	FUZZY()
	{ // suggest integrate Lucene
		@Override
		public Map<Integer, String> search(final String searchFor,
				final String searchWithin)
		{
			return null;
		}
	};

	/**
	 * 
	 * @param searchFor
	 * @param searchWithin
	 * @return A map of start points to matched text, e.g. if the word "test"
	 *         was matched at index 4 the map would contain 4 -> test
	 */
	public abstract Map<Integer, String> search(String searchFor,
			String searchWithin);
}
