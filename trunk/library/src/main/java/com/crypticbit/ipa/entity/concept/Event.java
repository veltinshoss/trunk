package com.crypticbit.ipa.entity.concept;

import java.util.Date;
import java.util.Map;

import com.crypticbit.ipa.entity.concept.wrapper.Tag;
import com.crypticbit.ipa.results.Location;


public interface Event {

	Map<Tag, ? extends Date> getWhen();
	Map<Tag, ? extends GeoLocation> getLocations();
	String getWhat();
	Map<Tag, ? extends Who> getWho();
	Location getFileLocation();
	String getDescription();
	
}
