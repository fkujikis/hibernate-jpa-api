// $Id$
package javax.persistence.criteria;

import javax.persistence.metamodel.AbstractCollection;
import javax.persistence.metamodel.Attribute;

/**
 * Represents an element of the from clause which may
 * function as the parent of Fetches.
 *
 * @param <Z>
 * @param <X>
 */
public interface FetchParent<Z, X> {

    /**
     *  Return the fetch joins that have been made from this type.
     *  @return fetch joins made from this type
     */
    java.util.Set<Fetch<X, ?>> getFetches();

    /**
     *  Fetch join to the specified attribute.
     *  @param assoc  target of the join
     *  @return the resulting fetch join
     */	
    <Y> Fetch<X, Y> fetch(Attribute<? super X, Y> assoc);

    /**
     *  Fetch join to the specified attribute using the given
     *  join type.
     *  @param assoc  target of the join
     *  @param jt  join type
     *  @return the resulting fetch join
     */	
    <Y> Fetch<X, Y> fetch(Attribute<? super X, Y> assoc, JoinType jt);

    /**
     *  Join to the specified collection. 
     *  @param assoc  target of the join
     *  @return the resulting join
     */
    <Y> Fetch<X, Y> fetch(AbstractCollection<? super X, ?, Y> assoc);
	
    /**
     *  Join to the specified collection using the given join type.
     *  @param assoc  target of the join
     *  @param jt  join type
     *  @return the resulting join
     */
    <Y> Fetch<X, Y> fetch(AbstractCollection<? super X, ?, Y> assoc, JoinType jt);
	

    //Untypesafe:
	
    /**
     *  Fetch join to the specified attribute or association.
     *  @param name  name of the attribute or association for the
     *               target of the join
     *  @return the resulting fetch join
     */	
    <Y> Fetch<X, Y> fetch(String assocName);

    /**
     *  Fetch join to the specified attribute or association using
     *  the given join type.
     *  @param name  name of the attribute or association for the
     *               target of the join
     *  @param jt  join type
     *  @return the resulting fetch join
     */	
    <Y> Fetch<X, Y> fetch(String assocName, JoinType jt);
}
