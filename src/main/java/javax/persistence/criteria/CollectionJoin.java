// $Id$
package javax.persistence.criteria;

import java.util.Collection;

/**
 * The interface CollectionJoin is the type of the result of
 * joining to a collection over an association or element 
 * collection that has been specified as a java.util.Collection.
 *
 * @param <Z> The source type of the join
 * @param <E> The element type of the target oCollection 
 */
public interface CollectionJoin<Z, E> 
		extends AbstractCollectionJoin<Z, Collection<E>, E> {

    /**
     * Return the metamodel representation for the collection.
     * @return metamodel type representing the Collection that is
     *         the target of the join
     */
    javax.persistence.metamodel.Collection<? super Z, E> getModel();
}
