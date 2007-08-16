//$Id: $
/**
 * JavaDoc copy/pastle from the Apache Lucene project
 * Available under the ASL 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package org.hibernate.search.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a property as indexable
 *
 * @author Emmanuel Bernard
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.METHOD, ElementType.FIELD} )
@Documented
public @interface Field {
	/**
	 * Field name, default to the JavaBean property name
	 */
	String name() default "";

	/**
	 * Should the value be stored in the document
	 */
	Store store() default Store.NO;

	/**
	 * Defines how the Field should be indexed
	 */
	Index index();

}
