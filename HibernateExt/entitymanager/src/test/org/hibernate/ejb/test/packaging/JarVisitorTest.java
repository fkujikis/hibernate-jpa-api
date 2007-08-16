//$Id$
package org.hibernate.ejb.test.packaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import junit.framework.TestCase;
import org.hibernate.ejb.packaging.ExplodedJarVisitor;
import org.hibernate.ejb.packaging.InputStreamZippedJarVisitor;
import org.hibernate.ejb.packaging.JarVisitor;
import org.hibernate.ejb.packaging.FileZippedJarVisitor;
import org.hibernate.ejb.test.pack.defaultpar.ApplicationServer;
import org.hibernate.ejb.test.pack.explodedpar.Carpet;

/**
 * @author Emmanuel Bernard
 */
public class JarVisitorTest extends TestCase {

	public void testHttp() throws Exception {
		URL url = JarVisitor.getJarURLFromURLEntry(
				new URL(
						"jar:http://www.ibiblio.org/maven/hibernate/jars/hibernate-annotations-3.0beta1.jar!/META-INF/persistence.xml"
				),
				"/META-INF/persistence.xml"
		);
		try {
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
		}
		catch (IOException ie) {
			//fail silently
			return;
		}
		JarVisitor visitor = JarVisitor.getVisitor( url, getFilters() );
		assertEquals( 0, visitor.getMatchingEntries()[0].size() );
		assertEquals( 0, visitor.getMatchingEntries()[1].size() );
		assertEquals( 0, visitor.getMatchingEntries()[2].size() );
	}

	public void testInputStreamZippedJar() throws Exception {
		String jarFileName = "file:./build/testresources/defaultpar.par";
		//JarVisitor jarVisitor = new ZippedJarVisitor( jarFileName, true, true );
		JarVisitor.Filter[] filters = getFilters();
		JarVisitor jarVisitor = new InputStreamZippedJarVisitor( new URL( jarFileName ), filters );
		assertEquals( "defaultpar", jarVisitor.getUnqualifiedJarName() );
		Set entries = jarVisitor.getMatchingEntries()[1];
		assertEquals( 3, entries.size() );
		JarVisitor.Entry entry = new JarVisitor.Entry( ApplicationServer.class.getName(), null );
		assertTrue( entries.contains( entry ) );
		entry = new JarVisitor.Entry( org.hibernate.ejb.test.pack.defaultpar.Version.class.getName(), null );
		assertTrue( entries.contains( entry ) );
		assertNull( ( (JarVisitor.Entry) entries.iterator().next() ).getInputStream() );
		assertEquals( 2, jarVisitor.getMatchingEntries()[2].size() );
		for (JarVisitor.Entry localEntry : (Set<JarVisitor.Entry>) jarVisitor.getMatchingEntries()[2] ) {
			assertNotNull( localEntry.getInputStream() );
			localEntry.getInputStream().close();
		}

//		Set<String> classes = jarVisitor.getClassNames();
//		assertEquals( 3, classes.size() );
//		assertTrue( classes.contains( ApplicationServer.class.getName() ) );
//		assertTrue( classes.contains( Mouse.class.getName() ) );
//		assertTrue( classes.contains( org.hibernate.ejb.test.pack.defaultpar.Version.class.getName() ) );
	}

	public void testZippedJar() throws Exception {
		String jarFileName = "file:./build/testresources/defaultpar.par";
		//JarVisitor jarVisitor = new ZippedJarVisitor( jarFileName, true, true );
		JarVisitor.Filter[] filters = getFilters();
		JarVisitor jarVisitor = new FileZippedJarVisitor( new URL( jarFileName ), filters );
		assertEquals( "defaultpar", jarVisitor.getUnqualifiedJarName() );
		Set entries = jarVisitor.getMatchingEntries()[1];
		assertEquals( 3, entries.size() );
		JarVisitor.Entry entry = new JarVisitor.Entry( ApplicationServer.class.getName(), null );
		assertTrue( entries.contains( entry ) );
		entry = new JarVisitor.Entry( org.hibernate.ejb.test.pack.defaultpar.Version.class.getName(), null );
		assertTrue( entries.contains( entry ) );
		assertNull( ( (JarVisitor.Entry) entries.iterator().next() ).getInputStream() );
		assertEquals( 2, jarVisitor.getMatchingEntries()[2].size() );
		for (JarVisitor.Entry localEntry : (Set<JarVisitor.Entry>) jarVisitor.getMatchingEntries()[2] ) {
			assertNotNull( localEntry.getInputStream() );
			localEntry.getInputStream().close();
		}
//		Set<String> classes = jarVisitor.getClassNames();
//		assertEquals( 3, classes.size() );
//		assertTrue( classes.contains( ApplicationServer.class.getName() ) );
//		assertTrue( classes.contains( Mouse.class.getName() ) );
//		assertTrue( classes.contains( org.hibernate.ejb.test.pack.defaultpar.Version.class.getName() ) );
	}

	public void testExplodedJar() throws Exception {
		String jarFileName = "./build/testresources/explodedpar.par";
		//JarVisitor jarVisitor = new ExplodedJarVisitor( jarFileName, true, true );
		JarVisitor.Filter[] filters = getFilters();
		JarVisitor jarVisitor = new ExplodedJarVisitor( jarFileName, filters );
		assertEquals( "explodedpar", jarVisitor.getUnqualifiedJarName() );
		Set[] entries = jarVisitor.getMatchingEntries();
		assertEquals( 1, entries[1].size() );
		assertEquals( 1, entries[0].size() );
		assertEquals( 1, entries[2].size() );

		JarVisitor.Entry entry = new JarVisitor.Entry( Carpet.class.getName(), null );
		assertTrue( entries[1].contains( entry ) );
		for (JarVisitor.Entry localEntry : (Set<JarVisitor.Entry>) jarVisitor.getMatchingEntries()[2] ) {
			assertNotNull( localEntry.getInputStream() );
			localEntry.getInputStream().close();
		}
//		Set<String> classes = jarVisitor.getClassNames();
//		assertEquals( 2, classes.size() );
//		assertEquals( 1, jarVisitor.getPackageNames().size() );
//		assertEquals( 1, jarVisitor.getHbmFiles().size() );
//		assertTrue( classes.contains( Carpet.class.getName() ) );
	}

	public void testDuplicateFilterExplodedJarExpectedfail() throws Exception {
		String jarFileName = "./build/testresources/explodedpar.par";
		//JarVisitor jarVisitor = new ExplodedJarVisitor( jarFileName, true, true );
		JarVisitor.Filter[] filters = getFilters();
		JarVisitor.Filter[] dupeFilters = new JarVisitor.Filter[filters.length * 2];
		int index = 0;
		for ( JarVisitor.Filter filter : filters ) {
			dupeFilters[index++] = filter;
		}
		filters = getFilters();
		for ( JarVisitor.Filter filter : filters ) {
			dupeFilters[index++] = filter;
		}
		JarVisitor jarVisitor = new ExplodedJarVisitor( jarFileName, dupeFilters );
		assertEquals( "explodedpar", jarVisitor.getUnqualifiedJarName() );
		Set[] entries = jarVisitor.getMatchingEntries();
		assertEquals( 1, entries[1].size() );
		assertEquals( 1, entries[0].size() );
		assertEquals( 1, entries[2].size() );
		for ( JarVisitor.Entry entry : (Set<JarVisitor.Entry>) entries[2] ) {
			InputStream is = entry.getInputStream();
			if ( is != null ) {
				assertTrue( 0 < is.available() );
				is.close();
			}
		}
		for ( JarVisitor.Entry entry : (Set<JarVisitor.Entry>) entries[5] ) {
			InputStream is = entry.getInputStream();
			if ( is != null ) {
				assertTrue( 0 < is.available() );
				is.close();
			}
		}

		JarVisitor.Entry entry = new JarVisitor.Entry( Carpet.class.getName(), null );
		assertTrue( entries[1].contains( entry ) );
	}

	private JarVisitor.Filter[] getFilters() {
		return new JarVisitor.Filter[]{
				new JarVisitor.PackageFilter( false, null ) {
					public boolean accept(String javaElementName) {
						return true;
					}
				},
				new JarVisitor.ClassFilter(
						false, new Class[]{
						Entity.class,
						MappedSuperclass.class,
						Embeddable.class}
				) {
					public boolean accept(String javaElementName) {
						return true;
					}
				},
				new JarVisitor.FileFilter( true ) {
					public boolean accept(String javaElementName) {
						return javaElementName.endsWith( "hbm.xml" ) || javaElementName.endsWith( "META-INF/orm.xml" );
					}
				}
		};
	}
}
