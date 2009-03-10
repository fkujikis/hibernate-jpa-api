// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import java.util.List;


/**
 * The interface CriteriaQuery defines functionality that is specific 
 * to top-level queries.
 *
 * A top-level query has an ordered list of selections.
 */

public interface CriteriaQuery extends AbstractQuery {
	
    /**
     * Specify the items that are to be returned in the query result.
     * Replaces the previously specified selections, if any.
     * @param selections  expressions specifying the items that
     *        are returned in the query result
     * @return the modified query
     */
    CriteriaQuery select(Selection<?>... selections);
	

    //override the return type only:

    /**
     * Modify the query to restrict the query result according
     * to the specified boolean expression.
     * Replaces the previously added restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery where(Expression<Boolean> restriction);

    /**
     * Modify the query to restrict the query result according 
     * to the conjunction of the specified restriction predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery where(Predicate... restrictions);

    /**
     * Specify the expressions that are used to form groups over
     * the query results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param grouping  zero or more grouping expressions
     * @return the modified query
     */
    CriteriaQuery group(Expression<?>... grouping);

    /**
     * Specify a restriction over the groups of the query.
     * Replaces the previous having restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery having(Expression<Boolean> restriction);

    /**
     * Specify restrictions over the groups of the query
     * according the conjunction of the specified restriction 
     * predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restrictions  zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery having(Predicate... restrictions);

    /**
     * Specify the ordering expressions that are used to
     * order the query results.
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
     * @return the modified query.
     */
    CriteriaQuery order(Order... o);

    /**
     * Specify whether duplicate query results will be eliminated.
     * A true value will cause duplicates to be eliminated.
     * A false value will cause duplicates to be retained.
     * If distinct has not been specified, duplicate results must
     * be retained.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param distinct  boolean value specifying whether duplicate
     *        results must be eliminated from the query result or
     *        whether they must be retained
     * @return the modified query.
     */
    CriteriaQuery distinct(boolean distinct);
    
    /**
     * Return the selection list of the query
     * @return the list of items to be returned in the query result
     */
    List<Selection<?>> getSelectionList();

    /**
     * Bind a parameter
     * @param  parameter to be bound
     * @param  value 
     * @return the modified query.
     */
    <T> CriteriaQuery setParameter(Parameter<T> param, T value);

    // Not sure what the intention here is:

    List<Result> getResultList();

    Result getSingleResult();
	
}
