//$Id: $
package org.hibernate.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.backend.Worker;
import org.hibernate.search.backend.WorkerFactory;
import org.hibernate.search.backend.BackendQueueProcessorFactory;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.DirectoryProviderFactory;
import org.hibernate.util.ReflectHelper;

/**
 * @author Emmanuel Bernard
 */
public class SearchFactory {
	private static ThreadLocal<WeakHashMap<Configuration, SearchFactory>> contexts =
			new ThreadLocal<WeakHashMap<Configuration, SearchFactory>>();
	static {
		Version.touch();
	}
	private Map<Class, DocumentBuilder<Object>> documentBuilders = new HashMap<Class, DocumentBuilder<Object>>();
	//keep track of the index modifiers per DirectoryProvider since multiple entity can use the same directory provider
	private Map<DirectoryProvider, ReentrantLock> lockableDirectoryProviders =
			new HashMap<DirectoryProvider, ReentrantLock>();
	private Worker worker;
	private BackendQueueProcessorFactory backendQueueProcessorFactory;


	public BackendQueueProcessorFactory getBackendQueueProcessorFactory() {
		return backendQueueProcessorFactory;
	}

	public void setBackendQueueProcessorFactory(BackendQueueProcessorFactory backendQueueProcessorFactory) {
		this.backendQueueProcessorFactory = backendQueueProcessorFactory;
	}

	public SearchFactory(Configuration cfg) {
		//yuk
		ReflectionManager reflectionManager = getReflectionManager( cfg );

		Class analyzerClass;
		String analyzerClassName = cfg.getProperty( Environment.ANALYZER_CLASS );
		if ( analyzerClassName != null ) {
			try {
				analyzerClass = ReflectHelper.classForName( analyzerClassName );
			}
			catch (Exception e) {
				throw new SearchException(
						"Lucene analyzer class '" + analyzerClassName + "' defined in property '" + Environment.ANALYZER_CLASS + "' could not be found.",
						e
				);
			}
		}
		else {
			analyzerClass = StandardAnalyzer.class;
		}
		// Initialize analyzer
		Analyzer analyzer;
		try {
			analyzer = (Analyzer) analyzerClass.newInstance();
		}
		catch (ClassCastException e) {
			throw new SearchException(
					"Lucene analyzer does not implement " + Analyzer.class.getName() + ": " + analyzerClassName
			);
		}
		catch (Exception e) {
			throw new SearchException( "Failed to instantiate lucene analyzer with type " + analyzerClassName );
		}

		Iterator iter = cfg.getClassMappings();
		DirectoryProviderFactory factory = new DirectoryProviderFactory();
		while ( iter.hasNext() ) {
			PersistentClass clazz = (PersistentClass) iter.next();
			Class<?> mappedClass = clazz.getMappedClass();
			if ( mappedClass != null ) {
				XClass mappedXClass = reflectionManager.toXClass( mappedClass );
				if ( mappedXClass != null && mappedXClass.isAnnotationPresent( Indexed.class ) ) {
					DirectoryProvider provider = factory.createDirectoryProvider( mappedXClass, cfg, this );
					if ( !lockableDirectoryProviders.containsKey( provider ) ) {
						lockableDirectoryProviders.put( provider, new ReentrantLock() );
					}
					final DocumentBuilder<Object> documentBuilder = new DocumentBuilder<Object>(
							mappedXClass, analyzer, provider, reflectionManager
					);

					documentBuilders.put( mappedClass, documentBuilder );
				}
			}
		}
		Set<Class> indexedClasses = documentBuilders.keySet();
		for ( DocumentBuilder builder : documentBuilders.values() ) {
			builder.postInitialize( indexedClasses );
		}
		WorkerFactory workerFactory = new WorkerFactory();
		workerFactory.configure( cfg, this );
		worker = workerFactory.createWorker();

	}

	//code doesn't have to be multithreaded because SF creation is not.
	//this is not a public API, should really only be used during the SessionFActory building
	public static SearchFactory getSearchFactory(Configuration cfg) {
		WeakHashMap<Configuration, SearchFactory> contextMap = contexts.get();
		if (contextMap == null) {
			contextMap = new WeakHashMap<Configuration, SearchFactory>( 2 );
			contexts.set( contextMap );
		}
		SearchFactory searchFactory = contextMap.get( cfg );
		if ( searchFactory == null) {
			searchFactory = new SearchFactory(cfg);

			contextMap.put( cfg, searchFactory );
		}
		return searchFactory;
	}


	public Map<Class, DocumentBuilder<Object>> getDocumentBuilders() {
		return documentBuilders;
	}

	public Map<DirectoryProvider, ReentrantLock> getLockableDirectoryProviders() {
		return lockableDirectoryProviders;
	}

	public Worker getWorker() {
		return worker;
	}

	//not happy about having it as a helper class but I don't want cfg to be associated with the SearchFactory
	public static ReflectionManager getReflectionManager(Configuration cfg) {
		ReflectionManager reflectionManager;
		try {
			//TODO introduce a ReflectionManagerHolder interface to avoid reflection
			//I want to avoid hard link between HAN and Validator for usch a simple need
			//reuse the existing reflectionManager one when possible
			reflectionManager =
					(ReflectionManager) cfg.getClass().getMethod( "getReflectionManager" ).invoke( cfg );

		}
		catch (Exception e) {
			reflectionManager = new JavaReflectionManager();
		}
		return reflectionManager;
	}

	public DirectoryProvider getDirectoryProvider(Class entity) {
		DocumentBuilder<Object> documentBuilder = getDocumentBuilders().get( entity );
		return documentBuilder == null ? null : documentBuilder.getDirectoryProvider();
	}
}
