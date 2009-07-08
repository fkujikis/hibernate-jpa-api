// $Id:$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import java.util.List;

/**
 * The CompoundSelection interface defines compound selection item
 * (tuple, array, or result of constructor).
 * @param <X> the type of the selection item
 */
public interface CompoundSelection<X> extends Selection<X> {

    /**
     * Return a selection items that were composed to form
     * the compound selection.
     * @return list of selection items
     */
    List<Selection<?>> getSelectionItems();
}