package monkeypuzzle.io.parser.sqlite.dynamicproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the joining of two tables
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlJoin
{
	/**
	 * The method name on the first interface representing the key
	 * 
	 * @return
	 */
	String firstKey();

	/**
	 * The method name on the second interface representing the key
	 * 
	 * @return
	 */
	String secondKey();
}
