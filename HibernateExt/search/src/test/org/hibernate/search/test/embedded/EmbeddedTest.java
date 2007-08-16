//$Id: $
package org.hibernate.search.test.embedded;

import java.util.List;

import org.hibernate.search.test.SearchTestCase;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;

/**
 * @author Emmanuel Bernard
 */
public class EmbeddedTest extends SearchTestCase {

	public void testEmbeddedIndexing() throws Exception {
		Tower tower = new Tower();
		tower.setName( "JBoss tower" );
		Address a = new Address();
		a.setStreet( "Tower place");
		a.setTower( tower );
		tower.setAddress( a );
		Owner o = new Owner();
		o.setName( "Atlanta Renting corp" );
		a.setOwnedBy( o );
		o.setAddress( a );

		Session s = openSession( );
		Transaction tx = s.beginTransaction();
		s.persist( tower );
		tx.commit();


		

		FullTextSession session = Search.createFullTextSession(s);
        QueryParser parser = new QueryParser("id", new StandardAnalyzer() );
        Query query;
        List result;

        query = parser.parse("address.street:place");
		result = session.createFullTextQuery(query).list();
        assertEquals( "unable to find property in embedded", 1, result.size() );

		query = parser.parse("address.ownedBy_name:renting");
		result = session.createFullTextQuery(query, Tower.class).list();
		assertEquals( "unable to find property in embedded", 1, result.size() );

		s.clear();

		tx = s.beginTransaction();
		Address address = (Address) s.get(Address.class, a.getId() );
		address.getOwnedBy().setName( "Buckhead community");
		tx.commit();


		s.clear();

		session = Search.createFullTextSession(s);

        query = parser.parse("address.ownedBy_name:buckhead");
		result = session.createFullTextQuery(query, Tower.class).list();
        assertEquals( "change in embedded not reflected in root index", 1, result.size() );

		s.clear();

		tx = s.beginTransaction();
		s.delete( s.get(Tower.class, tower.getId() ) );
		tx.commit();

		s.close();

	}

	public void testContainedIn() throws Exception {
		Tower tower = new Tower();
		tower.setName( "JBoss tower" );
		Address a = new Address();
		a.setStreet( "Tower place");
		a.setTower( tower );
		tower.setAddress( a );
		Owner o = new Owner();
		o.setName( "Atlanta Renting corp" );
		a.setOwnedBy( o );
		o.setAddress( a );

		Session s = openSession( );
		Transaction tx = s.beginTransaction();
		s.persist( tower );
		tx.commit();

		s.clear();

		tx = s.beginTransaction();
		Address address = (Address) s.get(Address.class, a.getId() );
		address.setStreet( "Peachtree Road NE" );
		tx.commit();

		s.clear();

		FullTextSession session = Search.createFullTextSession(s);
        QueryParser parser = new QueryParser("id", new StandardAnalyzer() );
        Query query;
        List result;

        query = parser.parse("address.street:peachtree");
		result = session.createFullTextQuery(query, Tower.class).list();
        assertEquals( "change in embedded not reflected in root index", 1, result.size() );

		s.clear();

		tx = s.beginTransaction();
		address = (Address) s.get(Address.class, a.getId() );
		address.getTower().setAddress( null );
		address.setTower( null );
		tx.commit();

		s.clear();

		session = Search.createFullTextSession(s);

        query = parser.parse("address.street:peachtree");
		result = session.createFullTextQuery(query, Tower.class).list();
        assertEquals( "breaking link fails", 0, result.size() );

		tx = s.beginTransaction();
		s.delete( s.get(Tower.class, tower.getId() ) );
		tx.commit();

		s.close();

	}

	protected Class[] getMappings() {
		return new Class[] {
				Tower.class,
				Address.class
		};
	}
}
