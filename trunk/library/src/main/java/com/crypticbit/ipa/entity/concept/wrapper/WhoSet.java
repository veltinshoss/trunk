package com.crypticbit.ipa.entity.concept.wrapper;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Set;

import com.crypticbit.ipa.entity.concept.wrapper.WhoSet.WhoEntry;
import com.crypticbit.ipa.entity.concept.wrapper.WhoTag.Field;
import com.crypticbit.ipa.entity.concept.wrapper.impl.WhoImpl;

public class WhoSet extends HashSet<WhoEntry>
{
	public static class WhoEntry extends SimpleImmutableEntry<Field, String>
	{

		private ConceptFactory conceptFactory;
		
		public WhoEntry(Field field, String value, ConceptFactory conceptFactory)
		{
			super(field, value);
			this.conceptFactory = conceptFactory;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof WhoEntry)
			{
				WhoEntry w = (WhoEntry) o;

				return this.getKey() == w.getKey()
						&& this.getKey().compare(this.getValue(), w.getValue(),conceptFactory.getDefaultTelephoneLocale());
			} else
				return false;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getKey().hashCode();
			result = prime * result + this.getKey().hashCode(this.getValue());
			return result;
		}

	}

}