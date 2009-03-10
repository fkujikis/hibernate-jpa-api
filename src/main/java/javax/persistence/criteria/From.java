// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Collection;
import javax.persistence.metamodel.List;
import javax.persistence.metamodel.Map;
import javax.persistence.metamodel.Set;

/**
 * Represents a bound type, usually an entity that appears in
 * the from clause, but may also be an embeddable belonging to
 * an entity in the from clause. 
 * Serves as a factory for Joins of associations, embeddables and 
 * collections belonging to the type, and for Paths of attributes 
 * belonging to the type.
 * @param <Z> 
 * @param <X> 
 */
public interface From<Z, X> extends Path<X>, FetchParent<Z, X> {

    /**
     *  Return the joins that have been made from this type.
     *  @return joins made from this type
     */
    java.util.Set<Join<X, ?>> getJoins();
	
    /**
     *  Join to the specified attribute.
     *  @param attribute  target of the join
     *  @return the resulting join
     */
    <Y> Join<X, Y> join(Attribute<? super X, Y> attribute);

    /**
     *  Join to the specified attribute, using the given join type.
     *  @param attribute  target of the join
     *  @param jt  join type 
     *  @return the resulting join
     */
    <Y> Join<X, Y> join(Attribute<? super X, Y> attribute, JoinType jt);
	

    /**
     *  Join to the specified Collection-valued attribute.
     *  @param collection  target of the join
     *  @return the resulting join
     */
    <Y> CollectionJoin<X, Y> join(Collection<? super X, Y> collection);

    /**
     *  Join to the specified Set-valued attribute.
     *  @param set  target of the join
     *  @return the resulting join
     */
    <Y> SetJoin<X, Y> join(Set<? super X, Y> set);

    /**
     *  Join to the specified List-valued attribute.
     *  @param list  target of the join
     *  @return the resulting join
     */
    <Y> ListJoin<X, Y> join(List<? super X, Y> list);

    /**
     *  Join to the specified Map-valued attribute.
     *  @param map  target of the join
     *  @return the resulting join
     */
    <K, V> MapJoin<X, K, V> join(Map<? super X, K, V> map);
	

    /**
     *  Join to the specified Collection-valued attribute, 
     *  using the given join type.
     *  @param collection  target of the join
     *  @param jt  join type 
     *  @return the resulting join
     */
    <Y> CollectionJoin<X, Y> join(Collection<? super X, Y> collection, JoinType jt);

    /**
     *  Join to the specified Set-valued attribute, using the given
     *  join type.
     *  @param set  target of the join
     *  @param jt  join type 
     *  @return the resulting join
     */
    <Y> SetJoin<X, Y> join(Set<? super X, Y> set, JoinType jt);

    /**
     *  Join to the specified List-valued attribute, using the
     *  given join type.
     *  @param list  target of the join
     *  @param jt  join type 
     *  @return the resulting join
     */
    <Y> ListJoin<X, Y> join(List<? super X, Y> list, JoinType jt);

    /**
     *  Join to the specified Map-valued attribute, using the
     *  given join type.
     *  @param map  target of the join
     *  @param jt  join type 
     *  @return the resulting join
     */
    <K, V> MapJoin<X, K, V> join(Map<? super X, K, V> map, JoinType jt);

	
    //Untypesafe:

    /**
     *  Join to the specified attribute. 
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @return the resulting join
     */
    <W, Y> Join<W, Y> join(String attributeName);	

    /**
     *  Join to the specified Collection-valued attribute. 
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @return the resulting join
     */
    <W, Y> CollectionJoin<W, Y> joinCollection(String attributeName);	

    /**
     *  Join to the specified Set-valued attribute. 
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @return the resulting join
     */
    <W, Y> SetJoin<W, Y> joinSet(String attributeName);	

    /**
     *  Join to the specified List-valued attribute. 
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @return the resulting join
     */
    <W, Y> ListJoin<W, Y> joinList(String attributeName);	

    /**
     *  Join to the specified Map-valued attribute. 
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @return the resulting join
     */
    <W, K, V> MapJoin<W, K, V> joinMap(String attributeName);	

    /**
     *  Join to the specified attribute, using the given
     *  join type.
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <W, Y> Join<W, Y> join(String attributeName, JoinType jt);	

    /**
     *  Join to the specified Collection-valued attribute, using 
     *  the given join type.
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <W, Y> CollectionJoin<W, Y> joinCollection(String attributeName, JoinType jt);	

    /**
     *  Join to the specified Set-valued attribute, using 
     *  the given join type.
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <W, Y> SetJoin<W, Y> joinSet(String attributeName, JoinType jt);	

    /**
     *  Join to the specified List-valued attribute, using 
     *  the given join type.
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <W, Y> ListJoin<W, Y> joinList(String attributeName, JoinType jt);	

    /**
     *  Join to the specified Mapn-valued attribute, using 
     *  the given join type.
     *  @param name  name of the attribute for the 
     *               target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <W, K, V> MapJoin<W, K, V> joinMap(String attributeName, JoinType jt);	
}
