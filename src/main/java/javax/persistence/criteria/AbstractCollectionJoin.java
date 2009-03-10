// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import javax.persistence.metamodel.AbstractCollection;

/**
 * The interface AbstractCollectionJoin defines functionality
 * that is common to joins to all collection types.  It is
 * not intended to be used directly in query construction.
 *
 * @param <Z> The source type
 * @param <C> The collection type
 * @param <E> The element type of the collection 
 */
public interface AbstractCollectionJoin<Z, C, E> 
		extends Join<Z, E> {

    /**
     * Return the metamodel representation for the collection.
     * @return metamodel type representing the collection that is
     *         the target of the join
     */
    AbstractCollection<? super Z, C, E> getModel();
	
    //TODO: do we need these????
    /*
      Predicate isEmpty();
      Predicate isNotEmpty();
	
      Expression<Integer> size();

      Predicate isMember(E elem);
      Predicate isNotMember(E elem);
	
      Predicate isMember(Expression<E> elem);
      Predicate isNotMember(Expression<E> elem);
    */
}
