//$Id$
package org.hibernate.ejb.test.connection;

import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;
import org.hibernate.ejb.HibernatePersistence;

/**
 * @author Emmanuel Bernard
 */
public class DataSourceInjectionTest extends TestCase {
	public void testDatasourceInjection() {
		PersistenceUnitInfoImpl info = new PersistenceUnitInfoImpl( new String[]{} );
		try {
			EntityManagerFactory emf = ( new HibernatePersistence() ).createContainerEntityManagerFactory( info, null );
			fail( "FakeDatasource should have been used" );
		}
		catch (FakeDataSourceException fde) {
			//success
		}
	}
}
