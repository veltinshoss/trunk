package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.crypticbit.ipa.entity.sqlite.Messages.MessageAfterIos6;

/**
 * Annotation to enumerate which of multiple sub-types could be returned -
 * depending on which is the best version match
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlValidateFieldsPresent {
}
