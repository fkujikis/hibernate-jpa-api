//$Id$
package javax.persistence;

/**
 * Expected an entity result, but none found.
 * 
 * @author Emmanuel Bernard
 */
public class NoResultException extends RuntimeException {

	public NoResultException() {
		super();
	}

	public NoResultException(String message, Throwable cause) {
		super( message, cause );
	}

	public NoResultException(String message) {
		super( message );
	}

	public NoResultException(Throwable cause) {
		super( cause );
	}
}
