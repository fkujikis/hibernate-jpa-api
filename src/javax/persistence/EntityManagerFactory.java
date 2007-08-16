//$Id$
package javax.persistence;

import java.util.Map;

public interface EntityManagerFactory {

	/**
	 * Create a new EntityManager.
	 * This method returns a new EntityManager instance each time
	 * it is invoked.
	 * The isOpen method will return true on the returned instance.
	 */
	EntityManager createEntityManager();

	/**
	 * Create a new EntityManager with the specified Map of
	 * properties.
	 * This method returns a new EntityManager instance each time
	 * it is invoked.
	 * The isOpen method will return true on the returned instance.
	 */
	EntityManager createEntityManager(Map map);

	/**
	 * Close the factory, releasing any resources that it holds.
	 * After a factory instance is closed, all methods invoked on
	 * it will throw an IllegalStateException, except for isOpen,
	 * which will return false. Once an EntityManagerFactory has
	 * been closed, all its entity managers are considered to be
	 * in the closed state.
	 */
	void close();

	/**
	 * Indicates whether the factory is open. Returns true
	 * until the factory has been closed.
	 */
	public boolean isOpen();
}