//$Id$
package javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is applied to a persistent field or property of an entity class or mapped superclass to denote
 * a composite primary key that is an embeddable class. The embeddable class must be annotated
 * as Embeddable.
 * 
 * @author Emmanuel Bernard
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmbeddedId {}
