/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package javax.persistence.spi;

import java.net.URL;
import java.util.Properties;
import java.util.List;
import javax.sql.DataSource;

/**
 * Interface implemented and used by the Container to pass
 * persistence metadata to the persistence provider as part of
 * the createContainerEntityManagerFactory() call. The provider
 * will use this metadata to obtain the mappings and initialize
 * its structures.
 */
public interface PersistenceInfo {

  /**
   * Returns the name of the EntityManager that is being created.
   * Corresponds to the <name> element in persistence.xml
   */
  public String getEntityManagerName();

  /**
   * Returns the name of the persistence provider implementation class.
   * Corresponds to the <provider> element in persistence.xml
   */
  public String getPersistenceProviderClassName();

  /**
   * Returns the JTA-enabled data source to be used by the persistence provider.
   * The data source corresponds to the named <jta-data-source> element in persistence.xml
   */
  public DataSource getJtaDataSource();

  /**
   * Returns the non-JTA-enabled data source to be used by the persistence provider
   * when outside the container, or inside the container when accessing data outside
   * the global transaction.
   * The data source corresponds to the named <non-jta-data-source> element in persistence.xml
   */
  public DataSource getNonJtaDataSource();

  /**
   * Returns the list of mapping file names that the persistence provider must
   * load to determine the mappings for the entity classes. The mapping files
   * must be in standard EJB mapping XML format, be uniquely named and be
   * resource-loadable from the application classpath. This list will not include
   * the entity-mappings.xml file if one was specified.
   * Each mapping file name corresponds to a <mapping-file> element in persistence.xml
   */
  public List<String> getMappingFileNames();

  /**
   * Returns the list of JAR file URLs that the persistence provider
   * must look in to find the entity classes that must be managed by
   * EntityManagers of this name. The persistence archive jar itself
   * will always be the last entry in the list.
   * Each jar file URL corresponds to a named <jar-file> element in persistence.xml
   */
  public List<URL> getJarFiles();

  /**
   * Returns the list of class names that the persistence provider
   * must inspect to see if it should add it to its set of managed
   * entity classes that must be managed by EntityManagers of this name.
   * Each class name corresponds to a named <class> element in persistence.xml
   */
  public List<String> getEntityclassNames();

  /**
   * Returns a Properties object that may contain vendor-specific properties
   * contained in the persistence.xml file.
   * Each property corresponds to a <property> element in persistence.xml
   */
  public Properties getProperties();

  /**
   * Returns a ClassLoader that the provider may use to load any
   * classes, resources, or open URLs.
   */
  public ClassLoader getClassLoader();

  /**
   * Returns a URL object that points to the persistence.xml file.
   * this is useful for providers that need to re-read the
   * persistence.xml file for some reason. If no persistence.xml
   * file was present in the persistence archive then null is returned.
   */
  public URL getPersistenceXmlFileUrl();

  /**
   * Returns a URL object that points to the entity-mappings.xml file.
   * If no entity-mappings.xml file was present in the persistence archive
   * then null is returned.
   */
  public URL getEntityMappingsXmlFileUrl();
}