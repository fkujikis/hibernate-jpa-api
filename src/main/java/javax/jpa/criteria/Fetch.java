package javax.persistence.criteria;

import javax.persistence.metamodel.Member;

/**
 * Represents a join-fetched association or attribute
 * @param <Z>
 * @param <X>
 */
public interface Fetch<Z, X> extends FetchParent<Z, X> {

    /**
     * Return the metamodel member corresponding to the fetch join.
     * @return metamodel member type for the join
     */
    Member<? extends Z, X> getMember();

    /**
     * Return the parent of the fetched item.
     * @return fetch parent
     */
    FetchParent<?, Z> getParent();

    /**
     * Return the join type used in the fetch join.
     * @return join type
     */
    JoinType getJoinType();
}
