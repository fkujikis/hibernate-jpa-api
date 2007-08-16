//$Id$
//EJB3 Specification Copyright 2004, 2005 Sun Microsystems, Inc.
package javax.persistence;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Emmanuel Bernard
 */
//TODO remove Package eventually
@Target({PACKAGE, TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface TableGenerator {

	String name();

	String table() default "";

	String catalog() default "";

	String schema() default "";

	String pkColumnName() default "";

	String valueColumnName() default "";

	String pkColumnValue() default "";

	int initialValue() default 0;

	int allocationSize() default 50;

	UniqueConstraint[] uniqueConstraints() default {};
}
