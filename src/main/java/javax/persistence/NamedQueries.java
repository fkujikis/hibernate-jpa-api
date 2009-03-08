//$Id: NamedQueries.java 11171 2007-02-08 03:40:51Z epbernard $
//EJB3 Specification Copyright 2004-2006 Sun Microsystems, Inc.
package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Specifies an array of named Java Persistence query language queries. Query names are scoped to the persistence unit.
 *
 * @author Emmanuel Bernard
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface NamedQueries {
	/**
	 * An array of named Java Persistence query language queries.
	 */
	NamedQuery [] value ();
}
