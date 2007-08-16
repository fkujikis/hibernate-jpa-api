//$Id$
package javax.persistence;

/**
 * @author Gavin King
 */
public class TransactionRequiredException extends PersistenceException {

	public TransactionRequiredException() {
		super();
	}

	public TransactionRequiredException(String message) {
		super( message );
	}

	public TransactionRequiredException(String message, Throwable cause) {
		super( message, cause );
	}

	public TransactionRequiredException(Throwable cause) {
		super( cause );
	}

}
