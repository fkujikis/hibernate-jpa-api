//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;

/**
 * this test should be fixed to have a proper model. currently a mix of subclass/joinedsubclass is in play.
 * @author max
 *
 */
public class InheritanceTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public InheritanceTest(String name) {
		super( name );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();		
	}
	
	public void testAllFilesExistence() {

		assertFalse(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Human.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Alien.hbm.xml") );		
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Animal.hbm.xml") );
	}
	
	public void testArtifactCollection() {
		
		assertEquals(3,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
		
	}
	
	public void testReadable() {
        Configuration cfg = new Configuration();
        
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Alien.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Human.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Animal.hbm.xml"));
        
        cfg.buildMappings();
        
    }
	
	
   
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"Aliens.hbm.xml"				
		};
	}
	    
	public static Test suite() {
		return new TestSuite(InheritanceTest.class);
	}

	
}
