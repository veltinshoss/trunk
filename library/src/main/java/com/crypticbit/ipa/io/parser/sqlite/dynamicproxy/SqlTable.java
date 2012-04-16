package com.crypticbit.ipa.io.parser.sqlite.dynamicproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the name of the SQL table to be used
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlTable
{
	String tableName() default "";
	String[] tableNames() default "";
}
