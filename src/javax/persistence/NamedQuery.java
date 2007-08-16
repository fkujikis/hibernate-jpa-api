//$Id$
//EJB3 Specification Copyright 2004, 2005 Sun Microsystems, Inc.
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;

/**
 * @author Emmanuel Bernard
 */
//TODO remove the mackage target
@Target({TYPE, PACKAGE}) @Retention(RUNTIME)
public @interface NamedQuery {
	String name();
	String query();
	QueryHint[] hints() default {};
}
