// $Id$
package javax.persistence.criteria;

/**
 * An object that defines an ordering over the query results.
 */
public interface Order {

   /**
    * Switch the ordering.
    */
    void reverse();

    /**
     * Whether ascending ordering is in effect.
     * @return boolean indicating whether ordering is ascending
     */
     boolean isAscending();

    /**
     * Return the expression that is used for ordering.
     * @return expression used for ordering
     */
    <T extends Comparable<T>> Expression<T> getExpression();
}

