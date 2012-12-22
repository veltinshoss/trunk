package com.crypticbit.ipa.io.parser.media;

import java.util.Date;
import java.util.logging.Level;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.results.Location;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;

public class MetadataImpl implements MetadataI {

	private Metadata metadata;
	private Location fileLocation;

	public MetadataImpl(Metadata metadata,Location fileLocation) {
		this.metadata = metadata;
		this.fileLocation = fileLocation;
	}

	@Override
	public Double getLong() {
		if (metadata != null) {
			try {
				Directory directory = metadata.getDirectory(GpsDirectory.class);
				if (directory != null
						&& directory
								.containsTag(GpsDirectory.TAG_GPS_LONGITUDE))
					return getCoordinateValue(directory,
							GpsDirectory.TAG_GPS_LONGITUDE,
							GpsDirectory.TAG_GPS_LONGITUDE_REF);
			} catch (Throwable e) {
				LogFactory.getLogger().log(Level.INFO,"Problem extracting geo metadata");
			}
		}
		return null;
	}

	@Override
	public Double getLat() {
		if (metadata != null) {
			try {
				Directory directory = metadata.getDirectory(GpsDirectory.class);
				if (directory != null
						&& directory.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
					return getCoordinateValue(directory,
							GpsDirectory.TAG_GPS_LATITUDE,
							GpsDirectory.TAG_GPS_LATITUDE_REF);
				}
			} catch (Throwable t) {
				LogFactory.getLogger().log(Level.INFO, "Problem extracting geo metadata");
			}
		}
		return null;

	}

	@Override
	public Date getWhen() {
		if (metadata != null) {
			try {
				Directory directory = metadata
						.getDirectory(ExifIFD0Directory.class);
				if (directory != null
						&& directory
								.containsTag(ExifIFD0Directory.TAG_DATETIME))
					return directory
							.getDate(ExifIFD0Directory.TAG_DATETIME);
			} catch (Throwable e) {
				LogFactory.getLogger().log(Level.INFO, "Problem extracting timemetadata");
			}
		}
		return null;

	}

	private Double getCoordinateValue(Directory dir, int tag, int ref_taf)
			throws Exception {
		try {
			Rational[] comps = dir.getRationalArray(tag);
			if (comps.length == 3) {
				int deg = comps[0].intValue();
				float min = comps[1].floatValue();
				float sec = comps[2].floatValue();
				sec += (min % 1) * 60;
				double value = (double) (deg + min / 60 + sec / 60 / 60);
				if (dir.getString(ref_taf).equalsIgnoreCase("W")
						|| dir.getString(ref_taf).equalsIgnoreCase("S"))
					value *= -1;
				return value;
			}
		} catch (Exception exc) {
			LogFactory.getLogger().log(Level.SEVERE,"parsing geo metadata",exc);
		}
		return null;
	}

	@Override
	public Location getFileLocation() {
		return fileLocation;
	}

}
