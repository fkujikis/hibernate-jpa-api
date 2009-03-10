// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import javax.persistence.metamodel.Collection;
import javax.persistence.metamodel.List;
import javax.persistence.metamodel.Map;
import javax.persistence.metamodel.Set;

/**
 * The interface Subquery defines functionality that is 
 * specific to subqueries.
 *
 * A subquery has an expression as its selection item.
 * @param <T> the type of the returned selection item.
 */

public interface Subquery<T> extends AbstractQuery, Expression<T> {

    /**
     * Return the query of which this is a subquery
     * @return the enclosing query or subquery
     */
    AbstractQuery getParent();
	
    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection, if any.
     * @param expression  expressions specifying the item that
     *        is returned in the query result
     * @return the modified subquery
     */
    Subquery<T> select(Expression<T> expression);
	
    //override the return type only:

    /**
     * Modify the subquery to restrict the result according
     * to the specified boolean expression.
     * Replaces the previously added restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified subquery
     */
    Subquery<T> where(Expression<Boolean> restriction);

    /**
     * Modify the subquery to restrict the result according 
     * to the conjunction of the specified restriction predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified subquery
     */
    Subquery<T> where(Predicate... restrictions);

    /**
     * Specify the expressions that are used to form groups over
     * the subquery results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param grouping  zero or more grouping expressions
     * @return the modified subquery
     */
    Subquery<T> group(Expression<?>... grouping);

    /**
     * Specify a restriction over the groups of the subquery.
     * Replaces the previous having restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified subquery
     */
    Subquery<T> having(Expression<Boolean> restriction);

    /**
     * Specify restrictions over the groups of the subquery
     * according the conjunction of the specified restriction 
     * predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified subquery
     */
    Subquery having(Predicate... restrictions);

    /**
     * Specify the ordering expressions that are used to
     * order the subquery results.
     * Replaces the previous ordering expressions, if any.
     * If no ordering expressions are specified, the previous
     * ordering, if any, is simply removed, and results will
     * be returned in no particular order.
     * The left-to-right sequence of the ordering expressions
     * determines the precedence, whereby the leftmost has highest
     * precedence.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param o  zero or more ordering expressions
     * @return the modified subquery.
     */
    Subquery<T> order(Order... o);

    /**
     * Specify whether duplicate query results will be eliminated.
     * A true value will cause duplicates to be eliminated.
     * A false value will cause duplicates to be retained.
     * If distinct has not been specified, duplicate results must
     * be retained.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param distinct  boolean value specifying whether duplicate
     *        results must be eliminated from the subquery result or
     *        whether they must be retained
     * @return the modified subquery.
     */
    Subquery<T> distinct(boolean distinct);
	
    /**
     * Return the selection expression
     * @return the item to be returned in the subquery result
     */
    Expression<T> getSelection();
	
    /**
     * Correlates a root of the enclosing query to a root of
     * the subquery and returns the subquery root.
     * @param parentRoot  a root of the containing query
     * @return subquery root
     */
    <Y> Root<Y> correlate(Root<Y> parentRoot);

    /**
     * Correlates a join of the enclosing query to a join of
     * the subquery and returns the subquery join.
     * @param parentJoin  join target of the containing query
     * @return subquery join
     */
    <X, Y> Join<X, Y> correlate(Join<X, Y> parentJoin);

    /**
     * Correlates a join to a Collection-valued association or
     * element collection in the enclosing query to a join of
     * the subquery and returns the subquery join.
     * @param parentCollection  join target of the containing query
     * @return subquery join
     */
    <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> parentCollection);

    /**
     * Correlates a join to a Set-valued association or
     * element collection in the enclosing query to a join of
     * the subquery and returns the subquery join.
     * @param parentSet  join target of the containing query
     * @return subquery join
     */
    <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentSet);

    /**
     * Correlates a join to a List-valued association or
     * element collection in the enclosing query to a join of
     * the subquery and returns the subquery join.
     * @param parentList  join target of the containing query
     * @return subquery join
     */
    <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentList);

    /**
     * Correlates a join to a Map-valued association or
     * element collection in the enclosing query to a join of
     * the subquery and returns the subquery join.
     * @param parentMap  join target of the containing query
     * @return subquery join
     */
    <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentMap);
}
