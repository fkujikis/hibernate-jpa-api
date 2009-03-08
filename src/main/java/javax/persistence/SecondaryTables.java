//$Id: SecondaryTables.java 11171 2007-02-08 03:40:51Z epbernard $
//EJB3 Specification Copyright 2004-2006 Sun Microsystems, Inc.
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
/**
 * This annotation is used to specify multiple secondary tables for an entity.
 *
 * @author Emmanuel Bernard
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface SecondaryTables {
	/**
	 * The secondary tables for an entity.
	 */
	SecondaryTable[] value();
}
