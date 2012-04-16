package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConceptIterator {

	public String tagPrefix();

	public Type type() default Type.RECURSE;

	public static enum Type {
		RECURSE, ITERATE
	}

}
