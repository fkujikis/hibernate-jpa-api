//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.util.DTDEntityResolver;

public class ManyToManyTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public ManyToManyTest(String name) {
		super( name, "cfg2hbmoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();		
	}
	
	public void testAllFilesExistence() {

		assertFalse(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/User.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Group.hbm.xml") );		
	}
	
	public void testArtifactCollection() {
		
		assertEquals(2,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
		
	}
	
	public void testReadable() {
        Configuration cfg = new Configuration();
        
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "User.hbm.xml"));
        cfg.addFile(new File(getOutputDir(), getBaseForMappings() + "Group.hbm.xml"));
        
        cfg.buildMappings();
        
    }
	
	public void testManyToMany() throws DocumentException {
		File outputXml = new File(getOutputDir(),  getBaseForMappings() + "User.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/many-to-many");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one many-to-many element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "entity-name" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.Group");
		
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one set element", 1, list.size());
		node = (Element) list.get(0);
		assertEquals(node.attribute( "table" ).getText(),"UserGroup");
		
		
	}
	
	
    private SAXReader getSAXReader() {
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setEntityResolver(new DTDEntityResolver() );
    	xmlReader.setValidation(true);
    	return xmlReader;
    }
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"UserGroup.hbm.xml"				
		};
	}
	    
	public static Test suite() {
		return new TestSuite(ManyToManyTest.class);
	}

}
