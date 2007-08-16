//$Id$
package org.hibernate.ejb.test.ejb3configuration;

import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.ejb.test.Cat;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;

/**
 * @author Emmanuel Bernard
 */
public class EventOverridingTest extends TestCase {

	public void testEventOverriding() throws Exception {
		EventListeners eventListeners = configuration.getEventListeners();
		assertEquals( 1, eventListeners.getPreInsertEventListeners().length );
		eventListeners.setPreInsertEventListeners( new PreInsertEventListener[]{} );
		Cat cat = new Cat();
		cat.setLength( 3 );
		cat.setAge( 34 );
		cat.setName( "Did" ); //should raise a validation exception
		EntityManagerFactory entityManagerFactory = configuration.createEntityManagerFactory();
		EntityManager em = entityManagerFactory.createEntityManager();
		assertEquals( "only validator should be present", 1,
				( (SessionImplementor) em.getDelegate() ).getListeners().getPreInsertEventListeners().length);
		em.close();
	}

	public void testEventPerProperties() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
		EntityManager em = emf.createEntityManager();
		assertEquals( "Only validator should be present", 1,
				( (SessionImplementor) em.getDelegate() ).getListeners().getPreInsertEventListeners().length);
		em.close();
		emf.close();
	}

	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Cat.class
		};
	}
}
