// $Id$
package javax.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/*
* Interface implemented by a persistence provider.
* The implementation of this interface that is to
* be used for a given EntityManager is specified in
* persistence.xml file in the persistence archive.
* This interface is invoked by the Container when it
* needs to create an EntityManagerFactory, or by the
* Persistence class when running outside the Container.
*/
  public interface PersistenceProvider {

   /**
    * Called by Persistence class when an EntityManagerFactory
    * is to be created.
    *
    * @param emName The name of the EntityManager configuration for the factory
    * @param map A Map of properties that may be useful by the provider vendor
    * @return EntityManagerFactory for the named EntityManager, null if the provider is not the right provider
    */
   public EntityManagerFactory createEntityManagerFactory(String emName, Map map);

   /**
    * Called by the Container when an EntityManagerFactory
    * is to be created.
    *
    * @param info The necessary metadata for the provider to use
    * @return EntityManagerFactory for the named EntityManager
    */
   public EntityManagerFactory createContainerEntityManagerFactory(PersistenceInfo info);
  }

