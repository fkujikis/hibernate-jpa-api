// $Id$
package javax.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.spi.PersistenceProvider;

/**
 * Bootstrap class that provides access to an EntityManagerFactory.
 */
public class Persistence {

	public static String PERSISTENCE_PROVIDER = "javax.persistence.spi.PeristenceProvider";

	protected static Set<PersistenceProvider> providers = new HashSet<PersistenceProvider>();

	public static EntityManagerFactory createEntityManagerFactory(String emName) {
		return createEntityManagerFactory( emName, null );
	}

	public static EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
		EntityManagerFactory emf = null;

		if ( providers.size() == 0 ) {
			findAllProviders();
		}
		for ( PersistenceProvider provider : providers ) {
			emf = provider.createEntityManagerFactory( emName, map );
			if ( emf != null ) break;
		}
		if ( emf == null ) {
			throw new PersistenceException( "No Persistence provider for EntityManager named " + emName );
		}
		return emf;
	}

	// Helper methods

	private static void findAllProviders() {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> resources = loader.getResources( "META-INF/services/" + PersistenceProvider.class.getName() );
			Set<String> names = new HashSet<String>();
			while ( resources.hasMoreElements() ) {
				URL url = resources.nextElement();
				InputStream is = url.openStream();
				try {
					names.addAll( providerNamesFromReader( new BufferedReader( new InputStreamReader( is ) ) ) );
				}
				finally {
					is.close();
				}
			}
			for ( String s : names ) {
				Class providerClass = loader.loadClass( s );
				providers.add( (PersistenceProvider) providerClass.newInstance() );
			}
		}
		catch (IOException e) {
			throw new PersistenceException( e );
		}
		catch (InstantiationException e) {
			throw new PersistenceException( e );
		}
		catch (IllegalAccessException e) {
			throw new PersistenceException( e );
		}
		catch (ClassNotFoundException e) {
			throw new PersistenceException( e );
		}
	}

	private static final Pattern nonCommentPattern = Pattern.compile( "^([^#]+)" );

	private static Set<String> providerNamesFromReader(BufferedReader reader) throws IOException {
		Set<String> names = new HashSet<String>();
		String line;
		while ( ( line = reader.readLine() ) != null ) {
			line = line.trim();
			Matcher m = nonCommentPattern.matcher( line );
			if ( m.find() ) {
				names.add( m.group().trim() );
			}
		}
		return names;
	}
}
