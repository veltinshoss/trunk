package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WhereTag {

	String tag() default "";
	Field field();

	public interface GeoSetter {
		public void setLongitude(Double value);
		public void setLatitude(Double value);
		public void setAccuracy(Double value);
		public void setAltitude(Integer value);
	}

	public static enum Field {
		LONGITUDE {
			@Override
			public void setValue(GeoSetter gs, Object value) {
				gs.setLongitude(Double.parseDouble(value.toString()));
			}
		},
		LATITUDE {
			@Override
			public void setValue(GeoSetter gs, Object value) {
				gs.setLatitude(Double.parseDouble(value.toString()));
			}
		},
		ACCURACY {
			@Override
			public void setValue(GeoSetter gs, Object value) {
				gs.setAccuracy((Double) value);
			}
		},
		ALTITUDE {
			@Override
			public void setValue(GeoSetter gs, Object value) {
				gs.setAltitude((Integer) value);
			}
		};
		public abstract void setValue(GeoSetter gs, Object value);
	}
}
