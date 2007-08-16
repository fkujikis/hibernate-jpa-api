//$Id$
package org.hibernate.ejb.test;

import java.util.Date;
import javax.persistence.EntityManager;

import org.hibernate.validator.InvalidStateException;

/**
 * @author Emmanuel Bernard
 */
public class ValidatorIntegrationTest extends TestCase {

	public void testPropertyValidation() throws Exception {
		EntityManager em = factory.createEntityManager();
		Cat cat = new Cat();
		cat.setAge( 33 );
		cat.setDateOfBirth( new Date() );
		cat.setName( "iti" );
		em.getTransaction().begin();
		try {
			em.persist( cat );
			em.flush();
			fail( "No validation" );
		}
		catch (InvalidStateException e) {
			//success
		}
		finally {
			em.getTransaction().rollback();
			em.close();
		}

	}

	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Cat.class
		};
	}
}
