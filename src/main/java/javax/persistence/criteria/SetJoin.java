package javax.persistence.criteria;

import java.util.Set;

/**
 * The interface SetJoin is the type of the result of
 * joining to a collection over an association or element 
 * collection that has been specified as a java.util.Set.
 *
 * @param <Z> The source type of the join
 * @param <E> The element type of the target Set 
 */

public interface SetJoin<Z, E> 
		extends AbstractCollectionJoin<Z, Set<E>, E> {

    /**
     * Return the metamodel representation for the set.
     * @return metamodel type representing the Set that is
     *         the target of the join
     */
    javax.persistence.metamodel.Set<? super Z, E> getModel();
}
