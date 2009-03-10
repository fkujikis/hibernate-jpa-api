// $Id$
package javax.persistence.metamodel;

/**
 *  Instances of the type IdentifiableType represent entity or 
 *  mapped superclass types.
 *
 *  @param <X> The represented entity or mapped superclass type.
 */
public interface IdentifiableType<X> extends ManagedType<X> {
	
    /**
     *  Return the attribute that corresponds to the id attribute of 
     *  the entity or mapped superclass.
     *  @param type  the type of the represented id attribute
     *  @return id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          name and type is not present in the identifiable type
     */
    <Y> Attribute<? super X, Y> getId(Class<Y> type);

    /**
     *  Return the attribute that corresponds to the version 
     *	  attribute
     *  of the entity or mapped superclass.
     *  @param type  the type of the represented version attribute
     *  @return version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     * 				given name and type is not present in the 
     *				identifiable type
     */
    <Y> Attribute<? super X, Y> getVersion(Class<Y> type);

    /**
     *  Return the attribute that corresponds to the id attribute 
     *  declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared id 
     * 					attribute
     *  @return declared id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          name and type is not present in the identifiable type
     */
    <Y> Attribute<X, Y> getDeclaredId(Class<Y> type);

    /**
     *  Return the attribute that corresponds to the version 
     *  attribute declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared version 
     *               attribute
     *  @return declared version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     * 				given name and type is not present in the 
     *				identifiable type
     */
    <Y> Attribute<X, Y> getDeclaredVersion(Class<Y> type);
	
    /**
     *  Return the identifiable type that corresponds to the most
     *  specific mapped superclass or entity extended by the entity 
     *  or mapped superclass.
     *  @return supertype of identifiable type
     */
    IdentifiableType<? super X> getSupertype();
}
