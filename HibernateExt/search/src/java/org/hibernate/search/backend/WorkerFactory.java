//$Id: $
package org.hibernate.search.backend;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.cfg.Configuration;
import org.hibernate.search.backend.impl.TransactionalWorker;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.Environment;
import org.hibernate.search.SearchException;
import org.hibernate.util.StringHelper;
import org.hibernate.util.ReflectHelper;

/**
 * @author Emmanuel Bernard
 */
public class WorkerFactory {
	private Map<Class, DocumentBuilder<Object>> documentBuilders;
	private Map<DirectoryProvider, ReentrantLock> lockableDirectoryProviders;
	private Configuration cfg;
	private SearchFactory searchFactory;

	public void configure(Configuration cfg,
			SearchFactory searchFactory) {
		this.searchFactory = searchFactory;
		this.cfg = cfg;
	}

	private static Properties getProperties(Configuration cfg) {
		Properties props = cfg.getProperties();
		Properties workerProperties = new Properties();
		for ( Map.Entry entry : props.entrySet() ) {
			String key = (String) entry.getKey();
			if ( key.startsWith( Environment.WORKER_PREFIX ) ) {
				//key.substring( Environment.WORKER_PREFIX.length() )
				workerProperties.setProperty( key, (String) entry.getValue() );
			}
		}
		return workerProperties;
	}

	public Worker createWorker() {
		Properties props = getProperties( cfg );
		String impl = props.getProperty( Environment.WORKER_SCOPE );
		Worker worker;
		if ( StringHelper.isEmpty( impl ) ) {
			worker = new TransactionalWorker();
		}
		else if ( "transaction".equalsIgnoreCase( impl ) ) {
			worker = new TransactionalWorker();
		}
		else {
			try {
				Class workerClass = ReflectHelper.classForName( impl, WorkerFactory.class );
				worker = (Worker) workerClass.newInstance();
			}
			catch (ClassNotFoundException e) {
				throw new SearchException("Unable to find worker class: " + impl, e );
			}
			catch (IllegalAccessException e) {
				throw new SearchException("Unable to instanciate worker class: " + impl, e );
			}
			catch (InstantiationException e) {
				throw new SearchException("Unable to instanciate worker class: " + impl, e );
			}
		}
		worker.initialize( props, searchFactory );
		return worker;
	}
}
