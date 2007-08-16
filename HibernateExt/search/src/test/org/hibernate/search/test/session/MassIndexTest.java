//$Id: $
package org.hibernate.search.test.session;

import java.util.List;
import java.sql.ResultSet;

import org.hibernate.search.test.SearchTestCase;
import org.hibernate.search.impl.FullTextSessionImpl;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.util.ContextHelper;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Hits;

/**
 * @author Emmanuel Bernard
 */
public class MassIndexTest extends SearchTestCase {

	public void testTransactional() throws Exception {
		FullTextSession s = Search.createFullTextSession( openSession() );
		Transaction tx = s.beginTransaction();
		int loop = 4;
		for (int i = 0 ; i < loop; i++) {
			Email email = new Email();
			email.setId( (long)i+1 );
			email.setTitle( "JBoss World Berlin" );
			email.setBody( "Meet the guys who wrote the software");
			s.persist( email );
		}
		tx.commit();
		s.close();

		//check non created object does get found!!1
		s = new FullTextSessionImpl( openSession() );
		tx = s.beginTransaction();
		QueryParser parser = new QueryParser("id", new StopAnalyzer() );
		List result = s.createFullTextQuery( parser.parse( "body:create" ) ).list();
		assertEquals( 0, result.size() );
		tx.commit();
		s.close();

		s = new FullTextSessionImpl( openSession() );
		s.getTransaction().begin();
		s.connection().createStatement().executeUpdate( "update Email set body='Meet the guys who write the software'");
		//insert an object never indexed
		s.connection().createStatement().executeUpdate( "insert into Email(id, title, body, header) values( + "
				+ (loop+1) + ", 'Bob Sponge', 'Meet the guys who create the software', 'nope')");
		s.getTransaction().commit();
		s.close();

		s = new FullTextSessionImpl( openSession() );
		tx = s.beginTransaction();
		parser = new QueryParser("id", new StopAnalyzer() );
		result = s.createFullTextQuery( parser.parse( "body:write" ) ).list();
		assertEquals( 0, result.size() );
		result = s.createCriteria( Email.class ).list();
		for (int i = 0 ; i < loop/2 ; i++)
			s.index( result.get( i ) );
		tx.commit(); //do the process
		s.index( result.get(loop/2) ); //do the process out of tx
		tx = s.beginTransaction();
		for (int i = loop/2+1 ; i < loop; i++)
			s.index( result.get( i ) );
		tx.commit(); //do the process
		s.close();

		s = Search.createFullTextSession( openSession() );
		tx = s.beginTransaction();
		//object never indexed
		Email email = (Email) s.get(Email.class, new Long(loop + 1) );
		s.index( email );
		tx.commit();
		s.close();

		//check non indexed object get indexed by s.index
		s = new FullTextSessionImpl( openSession() );
		tx = s.beginTransaction();
		result = s.createFullTextQuery( parser.parse( "body:create" ) ).list();
		assertEquals( 1, result.size() );
		tx.commit();
		s.close();

	}

	protected Class[] getMappings() {
		return new Class[] {
				Email.class
		};
	}
}
