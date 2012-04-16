package com.crypticbit.ipa.entity.concept;

public interface GeoLocation {

	Double getLongitude();

	Double getLatitude();

	Integer getAccuracy(); // metres

	Integer getAltitude(); // metres

}
