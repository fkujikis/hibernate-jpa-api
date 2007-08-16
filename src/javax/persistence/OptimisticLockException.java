//$Id: $
package javax.persistence;

/**
 * @author Emmanuel Bernard
 */
public class OptimisticLockException extends PersistenceException {
	public OptimisticLockException() {
		super();
	}

	public OptimisticLockException(Throwable cause) {
		super( cause );
	}

	public OptimisticLockException(String message) {
		super( message );
	}

	public OptimisticLockException(String message, Throwable cause) {
		super( message, cause );
	}
}
