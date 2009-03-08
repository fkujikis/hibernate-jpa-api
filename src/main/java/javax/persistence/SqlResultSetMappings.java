//$Id: SqlResultSetMapping.java 9044 2006-01-12 20:58:41 -0500 (jeu., 12 janv. 2006) epbernard $
//EJB3 Specification Copyright 2004 - 2006 Sun Microsystems, Inc.

package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to define one or more SqlResultSetMapping
 *
 * @author Emmanuel Bernard
 */
@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME)
public @interface SqlResultSetMappings {
	SqlResultSetMapping[] value();
}
