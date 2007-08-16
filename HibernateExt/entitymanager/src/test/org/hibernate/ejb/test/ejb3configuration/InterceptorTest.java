//$Id$
package org.hibernate.ejb.test.ejb3configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.ejb.test.Distributor;
import org.hibernate.ejb.test.Item;

/**
 * @author Emmanuel Bernard
 */
public class InterceptorTest extends TestCase {

	public void testInjectedInterceptor() {
		configuration.setInterceptor( new ExceptionInterceptor() );
		EntityManagerFactory emf = configuration.createEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		Item i = new Item();
		i.setName( "Laptop" );
		try {
			em.getTransaction().begin();
			em.persist( i );
			em.getTransaction().commit();
		}
		catch (IllegalStateException e) {
			assertEquals( ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage() );
		}
		finally {
			if ( em.getTransaction() != null && em.getTransaction().isActive() ) em.getTransaction().rollback();
			em.close();
			emf.close();
		}
	}

	public void testConfiguredInterceptor() {
		configuration.setProperty( HibernatePersistence.INTERCEPTOR, ExceptionInterceptor.class.getName() );
		EntityManagerFactory emf = configuration.createEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		Item i = new Item();
		i.setName( "Laptop" );
		try {
			em.getTransaction().begin();
			em.persist( i );
			em.getTransaction().commit();
			fail( "No interceptor" );
		}
		catch (IllegalStateException e) {
			assertEquals( ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage() );
		}
		finally {
			if ( em.getTransaction() != null && em.getTransaction().isActive() ) em.getTransaction().rollback();
			em.close();
			emf.close();
		}
	}

	public void testEmptyCreateEntityManagerFactoryAndPropertyUse() {
		configuration.setProperty( HibernatePersistence.INTERCEPTOR, ExceptionInterceptor.class.getName() );
		EntityManagerFactory emf = configuration.createEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		Item i = new Item();
		i.setName( "Laptop" );
		try {
			em.getTransaction().begin();
			em.persist( i );
			em.getTransaction().commit();
			fail( "No interceptor" );
		}
		catch (IllegalStateException e) {
			assertEquals( ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage() );
		}
		finally {
			if ( em.getTransaction() != null && em.getTransaction().isActive() ) em.getTransaction().rollback();
			em.close();
			emf.close();
		}
	}

	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Item.class,
				Distributor.class
		};
	}
}
