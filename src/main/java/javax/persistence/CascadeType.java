// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence;

/**
 * Defines the set of cascadable operations that are propagated to the associated entity.
 * The value cascade=ALL is equivalent to cascade={PERSIST, MERGE, REMOVE, REFRESH}.
 *
 * @author Emmanuel Bernard
 */
public enum CascadeType {
	/**
	 * Cascade all operations
	 */
	ALL,
	/**
	 * Cascade persist operations
	 */
	PERSIST,
	/**
	 * Cascade merge operations
	 */
	MERGE,
	/**
	 * Cascade remove operations
	 */
	REMOVE,
	/**
	 * Cascade refresh operations
	 */
	REFRESH 
}
