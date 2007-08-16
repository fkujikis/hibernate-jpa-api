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
//TODO remove the package target
@Target({TYPE, PACKAGE})
@Retention(RUNTIME)
public @interface NamedNativeQuery {
	String name();

	String query();

	QueryHint[] hints() default {};

	Class resultClass() default void.class;

	String resultSetMapping() default ""; // name of SQLResultSetMapping
}
