// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.metamodel;

/**
 * Instances of the type Collection represent persistent 
 * Collection-valued attributes.
 *
 * @param <X> The type the represented Collection belongs to
 * @param <E> The element type of the represented Collection
 */
public interface Collection<X, E> 
		extends AbstractCollection<X, java.util.Collection<E>, E> {}
