//$Id: $
package javax.persistence;

/**
 * @author Emmanuel Bernard
 */
public class EntityExistsException extends PersistenceException {
	public EntityExistsException() {
		super();
	}

	public EntityExistsException(Throwable cause) {
		super( cause );
	}

	public EntityExistsException(String message) {
		super( message );
	}

	public EntityExistsException(String message, Throwable cause) {
		super( message, cause );
	}
}
