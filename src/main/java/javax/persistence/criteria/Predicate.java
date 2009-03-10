// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import java.util.List;

/**
 * The type of a simple or compound predicate: a conjunction or
 * disjunction of restrictions.
 * A simple predicate is considered to be a conjunction with a
 * single conjunct.
 */
public interface Predicate extends Expression<Boolean> {
	
	public static enum BooleanOperator {
		AND, OR
	}
	
    /**
     * Return the boolean operator for the predicate.
     * If the predicate is simple, this is AND
     * @return boolean operator for the predicate
     */
    BooleanOperator getOperator();
    
    /**
     * Has negation been applied to the predicate?
     * @return boolean indicating if the predicate has been negated
     */
    boolean isNegated();

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     * @return list boolean expressions forming the predicate
     */
    List<Expression<Boolean>> getExpressions();
	
    /**
     * Add another operand to the predicate.
     * Whether the operand is added as a conjunct or disjunct is
     * determined by the predicate operator.
     * @return the resulting compound predicate
     */
    Predicate add(Expression<Boolean> s);
	
    /**
     * Apply negation to the predicate.
     * @return the negated predicate
     */
    Predicate negate();
}
