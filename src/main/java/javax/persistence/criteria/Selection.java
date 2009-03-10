// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

/**
 * The Selection interface defines an item that is returned by
 * a query.
 * @param <X>
 */

public interface Selection<X> {
    
    /**
     * Return the Java type of the selection.
     * @return the Java type of the selection item
     */
    Class<?> getJavaType();
}
