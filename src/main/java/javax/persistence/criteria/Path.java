// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.criteria;

import javax.persistence.metamodel.AbstractCollection;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Map;

/**
 * Represents a simple or compound attribute path from a 
 * bound type or collection, and is a "primitive" expression.
 * @param <X>  Type referenced by the path
 */
public interface Path<X> extends Expression<X> {

    Bindable<X> getModel(); //TODO: what does this return for a collection key, value? null?
    
    /**
     *  Return the parent "node" in the path.
     *  @return parent
     */
    Path<?> getParentPath();
	
    /**
     *  Return the path corresponding to the referenced 
     *  non-collection valued attribute.
     *  @param model attribute
     *  @return path corresponding to the referenced attribute
     */
    <Y> Path<Y> get(Attribute<? super X, Y> model);

    /**
     *  Return the path corresponding to the referenced 
     *  collection-valued attribute.
     *  @param collection collection-valued attribute
     *  @return path corresponding to the referenced attribute
     */
    <E, C extends java.util.Collection<E>> Expression<C> get(AbstractCollection<X, C, E> collection);

    /**
     *  Return the path corresponding to the referenced 
     *  map-valued attribute.
     *  @param collection map-valued attribute
     *  @return path corresponding to the referenced attribute
     */
    <K, V, M extends java.util.Map<K, V>> Expression<M> get(Map<X, K, V> collection);

    /**
     *  Return an expression corresponding to the type of the path.
     *  @return expression corresponding to the type of the path
     */
    Expression<Class<? extends X>> type();
	

    //Untypesafe:
	
    /**
     *  Return the path corresponding to the referenced 
     *  attribute.
     *  @param attName  name of the attribute
     *  @return path corresponding to the referenced attribute
     */
    <Y> Path<Y> get(String attName);
}
