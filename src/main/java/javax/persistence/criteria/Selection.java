// $Id$
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
