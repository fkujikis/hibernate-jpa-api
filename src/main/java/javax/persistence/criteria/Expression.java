// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import java.util.Collection;

/**
 * Type for query expressions.
 * @param <T> the type of the expression
 */
public interface Expression<T> extends Selection<T> {

    /**
     * Create a predicate to test whether the expression is null.
	 * @return predicate testing whether the expression is null
     */
    Predicate isNull();

    /**
     * Create a predicate to test whether the expression is
	 * not null.
	 * @return predicate testing whether the expression is not null.
     */
    Predicate isNotNull();

    /**
     * Create a predicate to test whether the expression is a member
	 * of the argument list.
	 * @param values The argument list
	 * @return predicate testing for membership in the list
     */
    Predicate in(Object... values);

    /**
     * Create a predicate to test whether the expression is a member
	 * of the argument list.
	 * @param values The argument list
	 * @return predicate testing for membership in the list
     */
    Predicate in(Expression<?>... values);

    /**
     * Create a predicate to test whether the expression is a member
	 * of the collection.
	 * @param values collection
	 * @return predicate testing for membership
     */
    Predicate in(Collection<?> values);

    /**
     * Create a predicate to test whether the expression is a member
	 * of the collection.
	 * @param values expression corresponding to collection
	 * @return predicate testing for membership
     */
    Predicate in(Expression<Collection<?>> values);

    /**
     * Perform a typecast upon the expression, returning new
	 * expression object.
	 * This method does not cause type conversion:
	 * the runtime type is not changed.
	 * Warning: may result in a runtime failure.
	 * @param type The cast type
	 * @return new expression of the given type
     */
    <X> Expression<X> as(Class<X> type);
}
