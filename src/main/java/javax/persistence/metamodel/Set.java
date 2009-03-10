// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.metamodel;

/**
 * Instances of the type Set represent persistent Set-valued
 * attributes.
 *
 * @param <X> The type the represented Set belongs to
 * @param <E> The element type of the represented Set
 */
public interface Set<X, E> 
		extends AbstractCollection<X, java.util.Set<E>, E> {}

