package org.hibernate.tool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.test.TestHelper;

public abstract class BaseTestCase extends TestCase {

	protected static final Log SKIP_LOG = LogFactory.getLog("org.hibernate.tool.test.SKIPPED");
	
	private File outputdir;
	
	protected File createBaseFile(String relative) {
		String root = System.getProperty("hibernatetool.test.supportdir", ".");
		return new File( root , relative);
	}
	
	public BaseTestCase(String name) {
		super(name);		
		this.outputdir = new File("toolstestoutput", getClass().getName());
	}
	
	public BaseTestCase(String name, String out) {
		super(name);		
		this.outputdir = new File("toolstestoutput", out);
	}

	protected void setUp() throws Exception {
		super.setUp();
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}
		
	}
	
	protected void tearDown() throws Exception {
		
		cleanupOutputDir();
		
		assertNoTables();
	}

	private void cleanupOutputDir() {
		if (getOutputDir()!=null) TestHelper.deleteDir(getOutputDir());
	}

	
	protected String findFirstString(String string, File file) {
		return TestHelper.findFirstString(string, file);
	}	
	
	protected void assertFileAndExists(File file) {
		assertTrue(file + " does not exist", file.exists() );
		assertTrue(file + " not a file", file.isFile() );		
		assertTrue(file + " does not have any contents", file.length()>0);
	}

	protected File getOutputDir() {
		return outputdir;
	}

	public void assertNoTables() throws SQLException {
		Configuration configuration = new Configuration();
		Settings testSettings = configuration.buildSettings();
		
		Connection con = null;
        try {
		
		con = testSettings.getConnectionProvider().getConnection();
		
		JDBCMetaDataDialect dialect = new JDBCMetaDataDialect();
		dialect.configure( testSettings.getConnectionProvider(), testSettings.getSQLExceptionConverter() );
		Iterator tables = dialect.getTables( testSettings.getDefaultCatalogName(), testSettings.getDefaultSchemaName(), null );
		
		assertHasNext( 0, tables );
        } finally {
        	testSettings.getConnectionProvider().closeConnection(con);	
        }
		
	}

	protected void assertHasNext(int expected, Iterator iterator) {
		assertHasNext(null, expected, iterator);
	}

	/**
	 * @param i
	 * @param iterator
	 */
	protected void assertHasNext(String reason, int expected, Iterator iterator) {
		int actual = 0;
		Object last = null;
		while(iterator.hasNext() && actual <= expected) {
			last = iterator.next();
			actual ++;
		}
		
		if(actual < expected) {
			throw new ComparisonFailure(reason==null?"Expected were less":reason, ""+expected, ""+actual);
		}
		
		if(actual > expected) {
			throw new ComparisonFailure((reason==null?"Expected were higher":reason)+", Last: " + last, ""+expected, ""+actual);
		}		
	}
	
	/**
	 * Intended to indicate that this test class as a whole is intended for
	 * a dialect or series of dialects.  Skips here (appliesTo = false), therefore
	 * simply indicate that the given tests target a particular feature of the
	 * current database...
	 *
	 * @param dialect
	 */
	public boolean appliesTo(Dialect dialect) {
		return true;
	}

	/**
	 * @return
	 */
	protected String getBaseForMappings() {
		return "org/hibernate/tool/";
	}


	protected void addMappings(String[] files, Configuration cfg) {
		for (int i=0; i<files.length; i++) {						
			if ( !files[i].startsWith("net/") ) {
				files[i] = getBaseForMappings() + files[i];
			}
			//System.out.println("bc in " + this.getClass() + " " + getBaseForMappings() + " " + files[i]);
			cfg.addResource( files[i], this.getClass().getClassLoader() );
		}
	}
	
	/** parse the url, fails if not valid xml. Does not validate against the DTD because they are remote */
	public Document assertValidXML(File url) {
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        Document document = null;
        try {
			document = reader.read(url);
		}
		catch (DocumentException e) {
			fail("Could not parse " + url + ":" + e); 
		}
		assertNotNull(document);
        return document;
    }
}

