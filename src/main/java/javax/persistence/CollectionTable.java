// $Id$
package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Hardy Ferentschik
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface CollectionTable {
	String name() default "";

	String catalog() default "";

	String schema() default "";

	JoinColumn[] joinColumns() default { };

	UniqueConstraint[] uniqueConstraints() default { };
}
