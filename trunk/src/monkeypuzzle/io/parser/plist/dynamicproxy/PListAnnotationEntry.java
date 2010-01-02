/**
 * 
 */
package monkeypuzzle.io.parser.plist.dynamicproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the entry in the Dictionary to be extracted
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PListAnnotationEntry
{
	String value();
}
