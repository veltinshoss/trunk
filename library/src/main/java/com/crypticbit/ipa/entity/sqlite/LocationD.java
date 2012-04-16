package com.crypticbit.ipa.entity.sqlite;

import java.util.List;

import com.crypticbit.ipa.entity.concept.Conceptable;
import com.crypticbit.ipa.entity.concept.wrapper.WhatTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag;
import com.crypticbit.ipa.entity.concept.wrapper.WhenTag.Field;
import com.crypticbit.ipa.entity.concept.wrapper.WhereTag;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlField;
import com.crypticbit.ipa.io.parser.sqlite.dynamicproxy.SqlTable;
import com.crypticbit.ipa.io.util.IphoneDate;


public interface LocationD extends List<LocationD.Location>, Conceptable {

	@SqlTable(tableName = "CellLocation")
	@WhatTag(name="Cell Location")
	public interface Location extends Conceptable {
		@WhereTag(field = WhereTag.Field.LATITUDE)
		@SqlField("Latitude")
		public Double getLatitude();

		@WhereTag(field = WhereTag.Field.LONGITUDE)
		@SqlField("Longitude")
		public Double getLongitude();
		
		@WhereTag(field = WhereTag.Field.ACCURACY)
		@SqlField("HorizontalAccuracy")
		public Double getHorizontalAccuracy();
		
		@WhenTag(tag="here",field = Field.DATE)
		@SqlField("Timestamp")
		public IphoneDate getDate();
	}
	
	@SqlTable(tableName = "WifiLocation")
	@WhatTag(name="Wifi Location")
	public interface WifiLocation extends Conceptable {
		@WhereTag(field = WhereTag.Field.LATITUDE)
		@SqlField("Latitude")
		public Double getLatitude();

		@WhereTag(field = WhereTag.Field.LONGITUDE)
		@SqlField("Longitude")
		public Double getLongitude();
		
		@WhereTag(field = WhereTag.Field.ACCURACY)
		@SqlField("HorizontalAccuracy")
		public Double getHorizontalAccuracy();
		
		@WhenTag(tag="here",field = Field.DATE)
		@SqlField("Timestamp")
		public IphoneDate getDate();
	}
}
