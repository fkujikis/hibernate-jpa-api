/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

/**
 * @author Josh Moore josh.moore@gmx.de
 * 
 */
public class PropertiesTest extends NonReflectiveTestCase {

	private ArtifactCollector artifactCollector;
	
	public PropertiesTest(String name) {
		super( name, "hbm2javaoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();

		artifactCollector = new ArtifactCollector();
		
		Exporter exporter = new POJOExporter( getCfg(), getOutputDir() );
		exporter.setArtifactCollector(artifactCollector);
		
		Exporter hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir());
		hbmexporter.setArtifactCollector(artifactCollector);
		
		exporter.start();
		hbmexporter.start();
	}	
	
	public void testNoGenerationOfEmbeddedPropertiesComponent() {
		assertEquals(2, artifactCollector.getFileCount("java"));
		assertEquals(2, artifactCollector.getFileCount("hbm.xml"));
	}
	
	public void testGenerationOfEmbeddedProperties() {
		
		// HACK: hbm.xml exporter actually does not support properties but whatever we do it should not remove emergencycontact from the list of properties being generated
		assertNotNull(findFirstString("emergencyContact", new File(getOutputDir(), "properties/PPerson.hbm.xml" ))); 
		
		assertNotNull(findFirstString("name", new File(getOutputDir(), "properties/PPerson.java" )));
		assertEquals("Embedded component/properties should not show up in .java", null, findFirstString("emergencyContact", new File(getOutputDir(), "properties/PPerson.java" )));		
	}
	
	public void testCompilable() {

		File file = new File( "compilable" );
		file.mkdir();

		ArrayList list = new ArrayList();
		list.add( new File( "src/testoutputdependent/properties/PropertiesUsage.java" )
				.getAbsolutePath() );		
		TestHelper.compile( getOutputDir(), file, TestHelper.visitAllFiles(
				getOutputDir(), list ) );

		TestHelper.deleteDir( file );
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}

	protected String[] getMappings() {
		return new String[] { "Properties.hbm.xml" };
	}


}
