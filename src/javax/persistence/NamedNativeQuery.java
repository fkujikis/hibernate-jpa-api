//$Id$
//EJB3 Specification Copyright 2004, 2005 Sun Microsystems, Inc.
package javax.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * @author Emmanuel Bernard
 */
@Target({METHOD, TYPE, PACKAGE}) @Retention(RUNTIME)
public @interface NamedNativeQuery {
	String name();
	String queryString();
	Class resultClass() default void.class;
	String resultSetMapping() default ""; // name of SQLResultSetMapping
}
