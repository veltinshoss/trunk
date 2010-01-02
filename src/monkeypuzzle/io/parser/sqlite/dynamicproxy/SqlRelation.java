package monkeypuzzle.io.parser.sqlite.dynamicproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the relationship used to find a rrelated table
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SqlRelation
{
	/**
	 * The method name to be used on this class to act as the foreign key
	 * 
	 * @return
	 */
	String foreign();

	/**
	 * The method name on the class representing the new table to be used at the
	 * primary key
	 * 
	 * @return
	 */
	String primary();
}
