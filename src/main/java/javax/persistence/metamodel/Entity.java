// $Id$
package javax.persistence.metamodel;

/**
 *  Instances of the type Entity represent entity types.
 *
 *  @param <X> The represented entity type.
 */
public interface Entity<X> extends IdentifiableType<X> {

    /**
     *  Return the entity name
     *  @return entity name
     */
    String getName();

    /**
     *  Return the Java type of the entity's id.
     *  @return Java type of id
     */
    Class<?> getIdJavaType();
}
