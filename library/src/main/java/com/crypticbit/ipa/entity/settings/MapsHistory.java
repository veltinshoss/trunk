package com.crypticbit.ipa.entity.settings;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptIterator;
import com.crypticbit.ipa.entity.concept.wrapper.ConceptIterator.Type;
import com.crypticbit.ipa.entity.concept.wrapper.DescriptionTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhereTag;
import com.crypticbit.ipa.io.parser.plist.dynamicproxy.PListAnnotationEntry;

/** Manually written based on data in iOS 4 device */
public interface MapsHistory extends Conceptable
{

	@WhatTag(name="Map history")
	public interface HistoryItem extends Conceptable
	{

		@DescriptionTag
		@PListAnnotationEntry("Query")
		public String getQuery();
		
		@WhereTag (field = WhereTag.Field.LATITUDE)
		@PListAnnotationEntry("Latitude")
		public Float getLatitude();
		
		@WhereTag (field = WhereTag.Field.LONGITUDE)
		@PListAnnotationEntry("Longitude")
		public Float getLongitude();
	}

	@ConceptIterator(tagPrefix = "", type=Type.ITERATE)
	@PListAnnotationEntry("HistoryItems")
	public HistoryItem[] getHistoryItems();

}