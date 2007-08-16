//$Id: $
package org.hibernate.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD } )
@Documented
/**
 * Specifies that an association (@*ToOne or @Embedded) is to be indexed
 * in the root entity index
 * It allows queries involving associated objects restrictions
 */
public @interface IndexedEmbedded {
	/**
	 * Field name prefix
	 * Default to 'propertyname.'
	 */
	String prefix() default ".";

	/**
	 * Stop indexing embedded elements when depth is reached
	 * depth=1 means the associated element is index, but not its embedded elements
	 * Default: infinite (an exception will be raised in case of class circular reference when infinite is chosen)
	 */
	int depth() default Integer.MAX_VALUE;
}
