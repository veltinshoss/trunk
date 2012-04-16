package com.crypticbit.ipa.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Creates a regular expression from some arbitrary subset of variant of a
 * simple regular expression language by replacing each element in the new
 * language with its corresponding version in the traditional language. Other
 * content is quoted to endure it doesn't interfere
 * 
 * @author Leo
 * 
 */
public class RegEx
{
	private static final Map<String, String> ENCODE_MAPPING = new HashMap<String, String>();
	static
	{
		ENCODE_MAPPING.put("?", ".?");
		ENCODE_MAPPING.put("*", ".*");
	}
	private Map<? extends Object, String> subs;
	// have to do this to ensure that keys are genuine unique and not a
	// substitution param which could then be used to replace again.
	// e.g. if we used keys of 1,2,3 etc, and one of the keys to sub was 2 then
	// we might find 1 converts to 2 and then is converted again. We therefore
	// have to find an impossible/unlikely key. hasCode would be great but isn't
	// guaranteed to be unique. therefore hasCode plus a unique identifer is
	// selected. the only possible risk is that that that string is a sub var -
	// but this is exceptionally unlikely
	private Map<Object, String> keys = new HashMap<Object, String>();
	public static final RegEx defaultRegEx = new RegEx(ENCODE_MAPPING);

	public RegEx(final Map<? extends Object, String> subs)
	{
		this.subs = subs;
		int i = 0;
		for (Object o : subs.keySet())
		{
			this.keys.put(o, "" + o.hashCode() + "," + i++);
		}
	}

	public String encode(final String toEncode)
	{
		String result = Pattern.quote(toEncode);
		/*
		 * if we don't do the double step then we could end up with a situation
		 * where: map a->b map b->c input(abc) output(ccc) rather than (bcc)
		 */
		for (Map.Entry<? extends Object, String> entry : this.subs.entrySet())
		{
			result = result.replaceAll(
					Pattern.quote(entry.getKey().toString()), "<##"
							+ this.keys.get(entry.getKey()) + "##>");
		}
		for (Map.Entry<? extends Object, String> entry : this.subs.entrySet())
		{
			result = result.replaceAll(Pattern.quote("<##"
					+ this.keys.get(entry.getKey()) + "##>"), "\\\\E"
					+ entry.getValue() + "\\\\Q");
		}
		return result;
	}
}
