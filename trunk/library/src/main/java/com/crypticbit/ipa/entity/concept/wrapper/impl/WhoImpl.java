package com.crypticbit.ipa.entity.concept.wrapper.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.crypticbit.ipa.entity.concept.Who;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptFactory;
import com.crypticbit.ipa.entity.concept.wrapper.WhoSet.WhoEntry;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag.Field;

public class WhoImpl extends LinkedList<WhoEntry> implements Who,
		com.crypticbit.ipa.entity.concept.wrapper.WhoTag.WhoSetter
{

	private ConceptFactory conceptFactory;

	public WhoImpl(ConceptFactory conceptFactory)
	{
		this.conceptFactory = conceptFactory;

	}

	@Override
	public void addIdentifier(final Field type, final String identifier)
	{
		if (identifier != null && identifier.length() > 0)
		{
			WhoEntry entry = new WhoEntry(type, identifier,conceptFactory);
			conceptFactory.add(this, entry);
			this.add(entry);
		}

	}

	public String toString()
	{
		String name = getName();
		if (name == null)
		{
			return getAttributes();
		} else
		{
			return name;
		}
	}

	private String getAttributes()
	{
		Collection<WhoEntry> whoSet = conceptFactory.get(this);
		if (whoSet == null)
			whoSet = this;

		if (whoSet.size() == 1)
			return whoSet.iterator().next().getValue();
		else
			return Arrays.toString(whoSet.toArray());

	}

	public String getName()
	{
		String fullName = getByField(Field.FULL_NAME);
		if (fullName != null)
			return fullName;
		else
		{
			String first = getByField(Field.FIRST_NAME);
			String surname = getByField(Field.SURNAME);
			String all = join(" ", first, surname);
			all = all.trim();
			if (all.length() == 0)
				return null;
			else
				return all;
		}
	}

	private static String join(String delimiter, String... coll)
	{
		if (coll == null || coll.length == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		for (String x : coll)
			if (x != null)
			{
				sb.append(x + delimiter);

			}

		if (sb.length() == 0)
			return "";
		if (sb.length() < delimiter.length())
			return sb.toString();

		sb.delete(sb.length() - delimiter.length(), sb.length());

		return sb.toString();
	}

	public String getByField(Field field)
	{
		Collection<WhoEntry> whoSet = conceptFactory.get(this);
		if (whoSet == null)
			whoSet = this;
		for (Entry<Field, String> s : whoSet)
		{
			if (s.getKey() == field)
				return s.getValue();
		}
		return null;
	}
}
