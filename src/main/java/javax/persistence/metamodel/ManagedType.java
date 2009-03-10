package javax.persistence.metamodel;

/**
 *  Instances of the type ManagedType represent entity, mapped 
 *  superclass, and embeddable types.
 *
 *  @param <X> The represented type.
 */
public interface ManagedType<X> extends Type<X>, Bindable<X> {
    /**
     *  Return the non-collection-valued attribute of the managed
     *  type that corresponds to the specified name in the
     *  represented type.
     *  @param name  the name of the represented attribute
     *  @return non-collection attribute with the given name
     */
    Attribute<? super X, ?> getAttribute(String name);

    /**
     *  Return the non-collection-valued attribute of the managed 
     *  type that corresponds to the specified name and Java type 
     *  in therepresented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return non-collection attribute with given name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <Y> Attribute<? super X, Y> getAttribute(String name, 
                                             Class<Y> type);

    /**
     *  Return the declared non-collection-valued attribute of the 
     *  managed type that corresponds to the specified name and Java 
     *  in the represented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return declared non-collection attribute of the given 
     *          name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <Y> Attribute<X, Y> getDeclaredAttribute(String name, 
                                             Class<Y> type);
	
    /**
     *  Return the non-collection-valued attributes of the 
     *  managed type.
     *  @return non-collection attributes
     */
    java.util.Set<Attribute<? super X, ?>> getAttributes();

    /**
     *  Return the non-collection-valued attributes declared by 
     *  the managed type.
     *  @return declared non-collection attributes
     */
    java.util.Set<Attribute<X, ?>> getDeclaredAttributes();
	
    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return Collection attribute of the given name and element
     *          type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */    
    <E> Collection<? super X, E> getCollection(String name, 
                                               Class<E> elementType);

    /**
     *  Return the Set-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return Set attribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> Set<? super X, E> getSet(String name, Class<E> elementType);

    /**
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return List attribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> List<? super X, E> getList(String name, Class<E> elementType);

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name and Java key and value
     *  types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return Map attribute of the given name and key and value
     *  types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <K, V> Map<? super X, K, V> getMap(String name, 
                                       Class<K> keyType, 
                                       Class<V> valueType);

    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared Collection attribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> Collection<X, E> getDeclaredCollection(String name, 
                                               Class<E> elementType);

    /**
     *  Return the Set-valued attribute declared by the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared Set attribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> Set<X, E> getDeclaredSet(String name, Class<E> elementType);

    /**
     *  Return the List-valued attribute declared by the managed 
     *  type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared List attribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <E> List<X, E> getDeclaredList(String name, Class<E> elementType);

    /**
     *  Return the Map-valued attribute declared by the managed 
     *  type that corresponds to the specified name and Java key 
     *  and value types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return declared Map attribute of the given name and key 
     *          and value types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    <K, V> Map<X, K, V> getDeclaredMap(String name, 
                                       Class<K> keyType, 
                                       Class<V> valueType);
    
    /**
     *  Return all collection-valued attributes of the managed type.
     *  @return collection valued attributes
     */
    java.util.Set<AbstractCollection<? super X, ?, ?>> getCollections();

    /**
     *  Return all collection-valued attributes declared by the 
     *  managedtype.
     *  @return declared collection valued attributes
     */
    java.util.Set<AbstractCollection<X, ?, ?>> getDeclaredCollections();
}
