// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

/**
 * Type for query expressions.
 * @param <T> the type of the expression
 */

public interface Expression<T> extends Selection<T> {

    /**
     * Return the Java type of the expression.
     * @return the Java type of the expression
     */
    Class<T> getJavaType();

    /**
     *  Apply a predicate to test whether the expression is null.
     *  @return predicate testing whether the expression is null
     */
    Predicate isNull();

    /**
     *  Apply a predicate to test whether the expression is not null.
     *  @return predicate testing whether the expression is not null.
     */
    Predicate isNotNull();
	
    /**
     * Apply a predicate to test whether the expression is a member
     * of the argument list.
     * @param values
     * @return predicate testing for membership in the list
     */
    Predicate in(Object... values);

    /**
     * Perform a typecast upon the expression.
     * Warning: may result in a runtime failure.
     * @param type 
     * @return expression
     */
    <X> Expression<X> as(Class<X> type);
}
