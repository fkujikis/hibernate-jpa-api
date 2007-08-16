//$Id$
package org.hibernate.ejb.test.callbacks;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.hibernate.ejb.test.Cat;
import org.hibernate.ejb.test.TestCase;

/**
 * @author Emmanuel Bernard
 */
public class CallbacksTest extends TestCase {

	public void testCallbackMethod() throws Exception {
		EntityManager em = factory.createEntityManager();
		Cat c = new Cat();
		c.setName( "Kitty" );
		c.setDateOfBirth( new Date( 90, 11, 15 ) );
		em.getTransaction().begin();
		em.persist( c );
		em.getTransaction().commit();
		em.clear();
		em.getTransaction().begin();
		c = em.find( Cat.class, c.getId() );
		assertFalse( c.getAge() == 0 );
		c.setName( "Tomcat" ); //update this entity
		em.getTransaction().commit();
		em.clear();
		em.getTransaction().begin();
		c = em.find( Cat.class, c.getId() );
		assertEquals( "Tomcat", c.getName() );
		em.getTransaction().commit();
		em.close();
	}

	public void testEntityListener() throws Exception {
		EntityManager em = factory.createEntityManager();
		Cat c = new Cat();
		c.setName( "Kitty" );
		c.setLength( 12 );
		c.setDateOfBirth( new Date( 90, 11, 15 ) );
		em.getTransaction().begin();
		int previousVersion = c.getManualVersion();
		em.persist( c );
		em.getTransaction().commit();
		em.getTransaction().begin();
		c = em.find( Cat.class, c.getId() );
		assertNotNull( c.getLastUpdate() );
		assertTrue( previousVersion < c.getManualVersion() );
		assertEquals( 12, c.getLength() );
		previousVersion = c.getManualVersion();
		c.setName( "new name" );
		em.getTransaction().commit();
		em.getTransaction().begin();
		c = em.find( Cat.class, c.getId() );
		assertTrue( previousVersion < c.getManualVersion() );
		em.getTransaction().commit();

		em.close();
	}


	public void testPostPersist() throws Exception {
		EntityManager em = factory.createEntityManager();
		Cat c = new Cat();
		em.getTransaction().begin();
		c.setLength( 23 );
		c.setAge( 2 );
		c.setName( "Beetle" );
		c.setDateOfBirth( new Date() );
		em.persist( c );
		em.getTransaction().commit();
		em.close();
		List ids = c.getIdList();
		Object id = c.getIdList().get( ids.size() - 1 );
		assertNotNull( id );
	}

	//Not a test since the spec did not make the proper change on listeners
	public void listenerAnnotation() throws Exception {
		EntityManager em = factory.createEntityManager();
		Translation tl = new Translation();
		em.getTransaction().begin();
		tl.setInto( "France" );
		em.persist( tl );
		tl = new Translation();
		tl.setInto( "Bimboland" );
		try {
			em.persist( tl );
			em.flush();
			fail( "Annotations annotated by a listener not used" );
		}
		catch (IllegalArgumentException e) {
			//success
		}
		finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	public void testPrePersistOnCascade() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Television tv = new Television();
		RemoteControl rc = new RemoteControl();
		em.persist( tv );
		em.flush();
		tv.setControl( rc );
		tv.init();
		em.flush();
		assertNotNull( rc.getCreationDate() );
		em.getTransaction().rollback();
	}

	public void testCallBackListenersHierarchy() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Television tv = new Television();
		em.persist( tv );
		tv.setName( "Myaio" );
		tv.init();
		em.flush();
		assertEquals( 1, tv.counter );
		em.getTransaction().rollback();
		em.close();
		assertEquals( 5, tv.communication );
		assertTrue( tv.isLast );
	}

	public void testException() throws Exception {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Rythm r = new Rythm();
		try {
			em.persist( r );
			em.flush();
			fail("should have raised an ArythmeticException:");
		}
		catch (ArithmeticException e) {
			//success
		}
		catch( Exception e ) {
			fail("should have raised an ArythmeticException:" + e.getClass() );
		}

		em.getTransaction().rollback();
		em.close();

	}

	public void testIdNullSetByPrePersist() throws Exception {
		Plant plant = new Plant();
		plant.setName( "Origuna plantula gigantic" );
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist( plant );
		em.flush();
		em.getTransaction().rollback();
		em.close();
	}

	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Cat.class,
				Translation.class,
				Television.class,
				RemoteControl.class,
				Rythm.class,
				Plant.class
		};
	}
}
