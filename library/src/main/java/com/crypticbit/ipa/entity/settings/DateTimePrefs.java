package com.crypticbit.ipa.entity.settings;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptIterator;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptIterator.Type;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhereTag;
import com.crypticbit.ipa.io.parser.plist.dynamicproxy.PListAnnotationEntry;

/** Manually written based on data in iOS 4 device */
public interface DateTimePrefs extends Conceptable
{

	@WhatTag(name = "DateTime prefs")
	public interface DateTimeItem extends Conceptable
	{

		@PListAnnotationEntry("countryName")
		public String getCountryName();

		@PListAnnotationEntry("localeCode")
		public String getLocaleCode();

		@DescriptionTag
		@PListAnnotationEntry("name")
		public String getName();

		@WhereTag(field = WhereTag.Field.LATITUDE)
		@PListAnnotationEntry("latitude")
		public Float getLatitude();

		@WhereTag(field = WhereTag.Field.LONGITUDE)
		@PListAnnotationEntry("longitude")
		public Float getLongitude();
	}

	@ConceptIterator(tagPrefix = "", type=Type.ITERATE)
	@PListAnnotationEntry("timezone")
	public DateTimeItem getDateTime();

}