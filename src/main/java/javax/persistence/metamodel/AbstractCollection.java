// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.metamodel;

/**
 * Instances of the type AbstractionCollection represent persistent
 * collection-valued attributes.
 *
 * @param <X> The type the represented collection belongs to
 * @param <C> The type of the represented collection
 * @param <E> The element type of the represented collection
 */
public interface AbstractCollection<X, C, E> 
		extends Member<X, C>, Bindable<E> {
	
	public static enum CollectionType {
		COLLECTION, SET, LIST, MAP
	}
	
	public static enum Multiplicity {
		MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
	}
	
    /**
     * Return the collection type
     * @return collection type
     */
    CollectionType getCollectionType();

    /**
     * Return the multiplicity
     * @return multiplicity
     */
    Multiplicity getMultiplicity();

    /**
     * Return the type representing the element type of the 
     * collection.
     * @return element type
     */
    Type<E> getElementType();
}
