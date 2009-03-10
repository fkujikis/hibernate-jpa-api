// $Id$
// EJB3 Specification Copyright 2004-2009 Sun Microsystems, Inc.
package javax.persistence.spi;

/**
 * Holds the global PersistenceProviderResolver instance.
 * If no PersistenceProviderResolver is set by the environment,
 * the default PersistenceProviderResolver is used. *
 * Implementations must be thread-safe.
 */
public class PersistenceProviderResolverHolder {
	/**
	 * Returns the current persistence provider resolver
	 */
	public static PersistenceProviderResolver getPersistenceProviderResolver() {
		return null;
	}

	/**
	 * Defines the persistence provider resolver used
	 */
	public static void setPersistenceProviderResolver(PersistenceProviderResolver resolver) {
	}
}
