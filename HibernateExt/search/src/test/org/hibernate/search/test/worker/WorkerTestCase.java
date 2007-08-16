//$Id: $
package org.hibernate.search.test.worker;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.search.Environment;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.event.FullTextIndexEventListener;
import org.hibernate.search.impl.FullTextSessionImpl;
import org.hibernate.search.store.FSDirectoryProvider;
import org.hibernate.search.test.SearchTestCase;

/**
 * @author Emmanuel Bernard
 */
public class WorkerTestCase extends SearchTestCase {

	protected void setUp() throws Exception {
		File sub = getBaseIndexDir();
		sub.mkdir();
		File[] files = sub.listFiles();
		for ( File file : files ) {
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

	public void testConcurrency() throws Exception {
		int nThreads = 15;
		ExecutorService es = Executors.newFixedThreadPool( nThreads );
		Work work = new Work( getSessions() );
		ReverseWork reverseWork = new ReverseWork( getSessions() );
		long start = System.currentTimeMillis();
		int iteration = 100;
		for ( int i = 0; i < iteration; i++ ) {
			es.execute( work );
			es.execute( reverseWork );
		}
		while ( work.count < iteration - 1 ) {
			Thread.sleep( 20 );
		}
		System.out.println( iteration + " iterations (8 tx per iteration) in " + nThreads + " threads: " + ( System
				.currentTimeMillis() - start ) );
	}

	protected class Work implements Runnable {
		private SessionFactory sf;
		public volatile int count = 0;

		public Work(SessionFactory sf) {
			this.sf = sf;
		}

		public void run() {
			Session s = sf.openSession();
			Transaction tx = s.beginTransaction();
			Employee ee = new Employee();
			ee.setName( "Emmanuel" );
			s.persist( ee );
			Employer er = new Employer();
			er.setName( "RH" );
			s.persist( er );
			tx.commit();
			s.close();

			s = sf.openSession();
			tx = s.beginTransaction();
			ee = (Employee) s.get( Employee.class, ee.getId() );
			ee.setName( "Emmanuel2" );
			er = (Employer) s.get( Employer.class, er.getId() );
			er.setName( "RH2" );
			tx.commit();
			s.close();

//			try {
//				Thread.sleep( 50 );
//			}
//			catch (InterruptedException e) {
//				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//			}

			s = sf.openSession();
			tx = s.beginTransaction();
			FullTextSession fts = new FullTextSessionImpl( s );
			QueryParser parser = new QueryParser( "id", new StopAnalyzer() );
			Query query;
			try {
				query = parser.parse( "name:emmanuel2" );
			}
			catch (ParseException e) {
				throw new RuntimeException( e );
			}
			boolean results = fts.createFullTextQuery( query ).list().size() > 0;
			//don't test because in case of async, it query happens before actual saving
			//if ( !results ) throw new RuntimeException( "No results!" );
			tx.commit();
			s.close();

			s = sf.openSession();
			tx = s.beginTransaction();
			ee = (Employee) s.get( Employee.class, ee.getId() );
			s.delete( ee );
			er = (Employer) s.get( Employer.class, er.getId() );
			s.delete( er );
			tx.commit();
			s.close();
			count++;
		}
	}

	protected class ReverseWork implements Runnable {
		private SessionFactory sf;

		public ReverseWork(SessionFactory sf) {
			this.sf = sf;
		}

		public void run() {
			Session s = sf.openSession();
			Transaction tx = s.beginTransaction();
			Employer er = new Employer();
			er.setName( "RH" );
			s.persist( er );
			Employee ee = new Employee();
			ee.setName( "Emmanuel" );
			s.persist( ee );
			tx.commit();
			s.close();

			s = sf.openSession();
			tx = s.beginTransaction();
			er = (Employer) s.get( Employer.class, er.getId() );
			er.setName( "RH2" );
			ee = (Employee) s.get( Employee.class, ee.getId() );
			ee.setName( "Emmanuel2" );
			tx.commit();
			s.close();

			s = sf.openSession();
			tx = s.beginTransaction();
			er = (Employer) s.get( Employer.class, er.getId() );
			s.delete( er );
			ee = (Employee) s.get( Employee.class, ee.getId() );
			s.delete( ee );
			tx.commit();
			s.close();
		}
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

	protected Class[] getMappings() {
		return new Class[]{
				Employee.class,
				Employer.class
		};
	}
}
