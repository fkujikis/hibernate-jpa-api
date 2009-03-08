package javax.persistence.metamodel;

/**
 * Provides access to the metamodel of persistent
 * entities in the persistence unit. 
 *
 * @param <X> The type of the represented entity, mapped
 *            mapped superclass, or embeddable.
 */
public interface Metamodel {

    /**
     *  Return the metamodel entity representing the entity type.
     *  @param clazz  the type of the represented entity
     *  @return the metamodel entity
     *  @throws IllegalArgumentException if not an entity
     */
    <X> Entity<X> entity(Class<X> clazz);

    /**
     *  Return the metamodel managed type representing the 
     *  entity, mapped superclass, or embeddable type.
     *  @param clazz  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    <X> ManagedType<X> type(Class<X> clazz);

    /**
     *  Return the metamodel embeddable type representing the
     *  embeddable type.
     *  @param clazz  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    <X> Embeddable<X> embeddable(Class<X> clazz);

    /**
     *  Return the metamodel managed types.
     *  @return the metamodel managed types
     */
    java.util.Set<ManagedType<?>> getManagedTypes();

    /**
     * Return the metamodel entity types.
     * @return the metamodel entity types
     */
    java.util.Set<Entity<?>> getEntities();

    /**
     * Return the metamodel embeddable types.
     * @return the metamodel embeddable types
     */
    java.util.Set<Embeddable<?>> getEmbeddables();
}
