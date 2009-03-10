// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import javax.persistence.metamodel.Member;

/**
 * A join to an entity or embeddable type.
 * @param <Z> The source type of the join
 * @param <X> The target type of the join
 */
public interface Join<Z, X> extends From<Z, X> {

    /**
     * Return the metamodel member corresponding to the join.
     * @return metamodel member type for the join
     */
    Member<? extends Z, X> getMember();

    /**
     * Return the parent of the join.
     * @return join parent
     */
    From<?, Z> getParent();

    /**
     * Return the join type.
     * @return join type
     */
    JoinType getJoinType();
}
