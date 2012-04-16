package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

@Retention(RetentionPolicy.RUNTIME)
public @interface WhenTag {
	
	String tag() default "";

	Field field() default Field.DATE;

	public interface WhenSetter {
		public void setDate(Date date);
	}

	public static enum Field {
		DATE {
			@Override
			public void setValue(WhenSetter gs, Object value) {
				gs.setDate((Date) value);
			}
		};
		public abstract void setValue(WhenSetter gs, Object value);
	}
}
