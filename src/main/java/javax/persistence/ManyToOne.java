//$Id: ManyToOne.java 11171 2007-02-08 03:40:51Z epbernard $
//EJB3 Specification Copyright 2004-2006 Sun Microsystems, Inc.
package javax.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static javax.persistence.FetchType.*;

/**
 * This annotation defines a single-valued association to another entity class that has
 * many-to-one multiplicity. It is not normally necessary to specify the target entity
 * explicitly since it can usually be inferred from the type of the object being referenced.
 *
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface ManyToOne {
	/**
	 * The entity class that is the target of the association.
	 *
	 * Defaults to the type of the field or property that stores the association
	 */
	Class targetEntity() default void.class;
	/**
	 * The operations that must be cascaded to the target of the association.
	 *
	 * By default no operations are cascaded.
	 */
	CascadeType[] cascade() default {};
	/**
	 * Whether the association should be lazily loaded or must be eagerly fetched.
	 * The EAGER strategy is a requirement on the persistence provider runtime that
	 * the associated entity must be eagerly fetched. The LAZY strategy is a hint to
	 * the persistence provider runtime.
	 */
	FetchType fetch() default EAGER;
	/**
	 * Whether the association is optional. If set to false then a non-null relationship must always exist.
	 */
	boolean optional() default true;
}
