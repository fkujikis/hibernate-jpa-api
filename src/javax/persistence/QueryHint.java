//$Id$
package javax.persistence;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Emmanuel Bernard
 */
@Target({})
@Retention(RUNTIME)
public @interface QueryHint {
	String name();

	String value();
}
