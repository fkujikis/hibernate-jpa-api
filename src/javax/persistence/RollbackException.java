//$Id: $
package javax.persistence;

/**
 * Exception occurs while commiting the transaction
 *
 * @author Emmanuel Bernard
 */
public class RollbackException extends PersistenceException {
	public RollbackException() {
		super();
	}

	public RollbackException(Throwable cause) {
		super( cause );
	}

	public RollbackException(String message) {
		super( message );
	}

	public RollbackException(String message, Throwable cause) {
		super( message, cause );
	}
}
