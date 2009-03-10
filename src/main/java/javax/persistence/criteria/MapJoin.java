package javax.persistence.criteria;

import java.util.Map;

/**
 * The interface MapJoin is the type of the result of
 * joining to a collection over an association or element 
 * collection that has been specified as a java.util.Map.
 *
 * @param <Z> The source type of the join
 * @param <K> The type of the target Map key
 * @param <V> The type of the target Map value
 */

public interface MapJoin<Z, K, V> 
		extends AbstractCollectionJoin<Z, Map<K, V>, V> {

    /**
     * Return the metamodel representation for the map.
     * @return metamodel type representing the Map that is
     *         the target of the join
     */
    javax.persistence.metamodel.Map<? super Z, K, V> getModel();
    
    /**
     * Specify a join over the map key.
     * @return result of joining over the map key
     */
    Join<Map<K, V>, K> joinKey();

    /**
     * Specify a join over the map key, using the given 
     * join type.
     * @param jt  join type
     * @return result of joining over the map key
     */    
    Join<Map<K, V>, K> joinKey(JoinType jt);
    
    /**
     * Return a path expression that corresponds to the map key.
     * @return Path corresponding to map key
     */
    Path<K> key();
    
    /**
     * Return a path expression that corresponds to the map value.
     * This method is for stylistic use only: it just returns this.
     * @return Path corresponding to the map value
     */
    Path<V> value(); //Unnecessary - just returns this
    
    /**
     * Return an expression that corresponds to the map entry.
     * @return Expression corresponding to the map entry
     */
    Expression<Map.Entry<K, V>> entry();
}
