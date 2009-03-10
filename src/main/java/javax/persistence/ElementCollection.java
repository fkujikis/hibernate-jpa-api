// $Id$
package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static javax.persistence.FetchType.LAZY;

/**
 * @author Hardy Ferentschik
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ElementCollection {
	Class targetClass() default void.class;

	FetchType fetch() default LAZY;
}
