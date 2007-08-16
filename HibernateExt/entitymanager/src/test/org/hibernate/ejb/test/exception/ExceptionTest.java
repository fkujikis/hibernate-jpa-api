//$Id: $
package org.hibernate.ejb.test.exception;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;

import org.hibernate.cfg.Environment;
import org.hibernate.ejb.test.TestCase;

/**
 * @author Emmanuel Bernard
 */
public class ExceptionTest extends TestCase {

	public void testOptimisticLockingException() throws Exception {
		EntityManager em = factory.createEntityManager();
		EntityManager em2 = factory.createEntityManager();
		em.getTransaction().begin();
		Music music = new Music();
		music.setName( "Old Country" );
		em.persist( music );
		em.getTransaction().commit();

		em2.getTransaction().begin();
		Music music2 = em2.find( Music.class, music.getId() );
		music2.setName( "HouseMusic" );
		em2.getTransaction().commit();
		em2.close();

		em.getTransaction().begin();
		music.setName( "Rock" );
		try {

			em.flush();
			fail("Should raise an optimistic lock exception");
		}
		catch( OptimisticLockException e) {
			//success
			assertEquals( music, e.getEntity() );
		}
		catch( Exception e ) {
			fail("Should raise an optimistic lock exception");
		}
		finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	public void testEntityNotFoundException() throws Exception {
		EntityManager em = factory.createEntityManager( );
		Music music = em.getReference( Music.class, new Integer(-1) );
		try {
			music.getName();
			fail("Non existent entity should raise an exception when state is accessed");
		}
		catch( EntityNotFoundException e ) {
			//success
		}
		finally {
			em.close();
		}
	}

	@Override
	public Map getConfig() {
		Map config = super.getConfig();
		config.put( Environment.BATCH_VERSIONED_DATA, "false");
		return config;
	}

	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Music.class
		};
	}
}
