// $Id$
package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Hardy Ferentschik
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Cacheable {
	boolean value() default true;
}
