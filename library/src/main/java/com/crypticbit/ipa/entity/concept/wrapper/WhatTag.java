package com.crypticbit.ipa.entity.concept.wrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WhatTag {

	String name();

}
