//$Id$
package org.hibernate.ejb.test.instrument;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bernard
 */
public class InterceptFieldClassFileTransformerTest extends TestCase {
	public void testEnhancement() throws Exception {
		List<String> entities = new ArrayList<String>();
		entities.add( "org.hibernate.ejb.test.instrument.Simple" );
		InstrumentedClassLoader cl = new InstrumentedClassLoader( Thread.currentThread().getContextClassLoader() );
		cl.setEntities( entities );
		Class interceptedFieldEnabled = cl.loadClass( "net.sf.cglib.transform.impl.InterceptFieldEnabled" );
		Class clazz = cl.loadClass( entities.get( 0 ) );
		//clazz = cl.loadClass( "org.hibernate.ejb.test.instrument.Simple" );
		assertTrue( interceptedFieldEnabled.isAssignableFrom( clazz ) );
		clazz.getName();
	}
}
