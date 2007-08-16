package org.hibernate.tool.hbm2x;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Hbm2XAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite( "Test for org.hibernate.tool.hbm2x" );
		//$JUnit-BEGIN$
		suite.addTestSuite( Hbm2JavaConstructorTest.class );
		suite
				.addTestSuite( Hbm2JavaBidirectionalIndexedCollectionMappingTest.class );
		suite.addTestSuite( Hbm2JavaTest.class );
		suite.addTestSuite( DefaultSchemaCatalogTest.class );
		suite.addTestSuite( GenerateFromJDBCTest.class );
		suite.addTestSuite( HashcodeEqualsTest.class );
		suite.addTestSuite( Hbm2DaoTest.class );
		suite.addTestSuite( XMLPrettyPrinterTest.class );
		suite.addTestSuite( Hbm2JavaEjb3Test.class );
		suite.addTestSuite( OtherCfg2HbmTest.class );
		suite.addTestSuite( Hbm2EJBDaoTest.class );
		suite.addTestSuite( Hbm2CfgTest.class );
		suite.addTestSuite( GenericExporterTest.class );
		suite.addTestSuite( Hbm2HibernateDAOTest.class );
		suite.addTestSuite( DocExporterTest.class );		
		suite.addTestSuite( PropertiesTest.class );
		//$JUnit-END$
		return suite;
	}

}
