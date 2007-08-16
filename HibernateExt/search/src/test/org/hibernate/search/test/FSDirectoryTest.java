//$Id: LuceneTest.java 10014 2006-06-12 09:56:27 -0700 (lun., 12 juin 2006) epbernard $
package org.hibernate.search.test;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.search.Environment;
import org.hibernate.search.event.FullTextIndexEventListener;
import org.hibernate.search.store.FSDirectoryProvider;

/**
 * @author Gavin King
 */
public class FSDirectoryTest extends SearchTestCase {


	protected void setUp() throws Exception {
		File sub = getBaseIndexDir();
		sub.mkdir();
		File[] files = sub.listFiles();
		for (File file : files) {
			if ( file.isDirectory() ) {
				delete( file );
			}
		}
		//super.setUp(); //we need a fresh session factory each time for index set up
		buildSessionFactory( getMappings(), getAnnotatedPackages(), getXmlFiles() );
	}

	private File getBaseIndexDir() {
		File current = new File( "." );
		File sub = new File( current, "indextemp" );
		return sub;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		File sub = getBaseIndexDir();
		delete( sub );
	}

	private void delete(File sub) {
		if ( sub.isDirectory() ) {
			for ( File file : sub.listFiles() ) {
				delete( file );
			}
			sub.delete();
		}
		else {
			sub.delete();
		}
	}

	public void testEventIntegration() throws Exception {


		Session s = getSessions().openSession();
		s.getTransaction().begin();
		s.persist(
				new Document( "Hibernate in Action", "Object/relational mapping with Hibernate", "blah blah blah" )
		);
		s.getTransaction().commit();
		s.close();
		IndexReader reader = IndexReader.open( new File( getBaseIndexDir(), "Documents" ) );
		try {
			int num = reader.numDocs();
			assertEquals( 1, num );
			TermDocs docs = reader.termDocs( new Term( "Abstract", "Hibernate" ) );
			org.apache.lucene.document.Document doc = reader.document( docs.doc() );
			assertFalse( docs.next() );
			docs = reader.termDocs( new Term( "Title", "Action" ) );
			doc = reader.document( docs.doc() );
			assertFalse( docs.next() );
			assertEquals( "1", doc.getField( "id" ).stringValue() );
		}
		finally {
			reader.close();
		}

		s = getSessions().openSession();
		s.getTransaction().begin();
		Document entity = (Document) s.get( Document.class, new Long( 1 ) );
		entity.setSummary( "Object/relational mapping with EJB3" );
		s.persist( new Document( "Seam in Action", "", "blah blah blah blah" ) );
		s.getTransaction().commit();
		s.close();

		reader = IndexReader.open( new File( getBaseIndexDir(), "Documents" ) );
		try {
			int num = reader.numDocs();
			assertEquals( 2, num );
			TermDocs docs = reader.termDocs( new Term( "Abstract", "ejb" ) );
			assertTrue( docs.next() );
			org.apache.lucene.document.Document doc = reader.document( docs.doc() );
			assertFalse( docs.next() );
		}
		finally {
			reader.close();
		}

		s = getSessions().openSession();
		s.getTransaction().begin();
		s.delete( entity );
		s.getTransaction().commit();
		s.close();

		reader = IndexReader.open( new File( getBaseIndexDir(), "Documents" ) );
		try {
			int num = reader.numDocs();
			assertEquals( 1, num );
			TermDocs docs = reader.termDocs( new Term( "title", "seam" ) );
			assertTrue( docs.next() );
			org.apache.lucene.document.Document doc = reader.document( docs.doc() );
			assertFalse( docs.next() );
			assertEquals( "2", doc.getField( "id" ).stringValue() );
		}
		finally {
			reader.close();
		}

		s = getSessions().openSession();
		s.getTransaction().begin();
		s.delete( s.createCriteria( Document.class ).uniqueResult() );
		s.getTransaction().commit();
		s.close();
	}

	public void testBoost() throws Exception {
		Session s = getSessions().openSession();
		s.getTransaction().begin();
		s.persist(
				new Document( "Hibernate in Action", "Object and Relational", "blah blah blah" )
		);
		s.persist(
				new Document( "Object and Relational", "Hibernate in Action", "blah blah blah" )
		);
		s.getTransaction().commit();
		s.close();

		IndexSearcher searcher = new IndexSearcher( new File( getBaseIndexDir(), "Documents" ).getCanonicalPath() );
		try {
			QueryParser qp = new QueryParser( "id", new StandardAnalyzer() );
			Query query = qp.parse( "title:Action OR Abstract:Action" );
			Hits hits = searcher.search( query );
			assertEquals( 2, hits.length() );
			assertTrue( hits.score( 0 ) == 2 * hits.score( 1 ) );
			assertEquals( "Hibernate in Action", hits.doc( 0 ).get( "title" ) );
		}
		finally {
			searcher.close();
		}


		s = getSessions().openSession();
		s.getTransaction().begin();
		List list = s.createQuery( "from Document" ).list();
		for ( Document document : (List<Document>) list ) {
			s.delete( document );
		}
		s.getTransaction().commit();
		s.close();
	}

	protected Class[] getMappings() {
		return new Class[]{
				Document.class
		};
	}

	protected void configure(org.hibernate.cfg.Configuration cfg) {
		File sub = getBaseIndexDir();
		cfg.setProperty( "hibernate.search.default.indexBase", sub.getAbsolutePath() );
		cfg.setProperty( "hibernate.search.Clock.directory_provider", FSDirectoryProvider.class.getName() );
		cfg.setProperty( Environment.ANALYZER_CLASS, StopAnalyzer.class.getName() );
		FullTextIndexEventListener del = new FullTextIndexEventListener();
		cfg.getEventListeners().setPostDeleteEventListeners( new PostDeleteEventListener[]{del} );
		cfg.getEventListeners().setPostUpdateEventListeners( new PostUpdateEventListener[]{del} );
		cfg.getEventListeners().setPostInsertEventListeners( new PostInsertEventListener[]{del} );
	}

}

