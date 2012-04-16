package com.crypticbit.ipa.io.util;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@SuppressWarnings("serial")
public final class TreeMapContainingSet<K, C> extends TreeMap<K, Set<C>>
{
	public void putSet(final K key, final C value)
	{
		if (get(key) == null)
		{
			put(key, new TreeSet<C>());
		}
		get(key).add(value);
	}
}