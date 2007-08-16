//$Id: $
package org.hibernate.search.backend.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hibernate.Hibernate;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.search.Environment;
import org.hibernate.search.SearchException;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.BackendQueueProcessorFactory;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.QueueingProcessor;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.backend.impl.jms.JMSBackendQueueProcessorFactory;
import org.hibernate.search.backend.impl.lucene.LuceneBackendQueueProcessorFactory;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.util.ReflectHelper;

/**
 * Batch work until #performWork is called.
 * The work is then executed synchronously or asynchronously
 *
 * @author Emmanuel Bernard
 */
public class BatchedQueueingProcessor implements QueueingProcessor {
	private boolean sync;
	private ExecutorService executorService;
	private BackendQueueProcessorFactory backendQueueProcessorFactory;
	private SearchFactory searchFactory;

	public BatchedQueueingProcessor(SearchFactory searchFactory,
									Properties properties) {
		this.searchFactory = searchFactory;
		//default to sync if none defined
		this.sync = !"async".equalsIgnoreCase( properties.getProperty( Environment.WORKER_EXECUTION ) );

		//default to a simple asynchronous operation
		int min = Integer.parseInt(
				properties.getProperty( Environment.WORKER_THREADPOOL_SIZE, "1" ).trim()
		);
		//no queue limit
		int queueSize = Integer.parseInt(
				properties.getProperty( Environment.WORKER_WORKQUEUE_SIZE, Integer.toString( Integer.MAX_VALUE ) ).trim()
		);
		if ( !sync ) {
			/**
			 * choose min = max with a sizable queue to be able to
			 * actually queue operations
			 * The locking mechanism preventing much of the scalability
			 * anyway, the idea is really to have a buffer
			 * If the queue limit is reached, the operation is executed by the main thread
			 */
			executorService = new ThreadPoolExecutor(
					min, min, 60, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(queueSize),
					new ThreadPoolExecutor.CallerRunsPolicy()
			);
		}
		String backend = properties.getProperty( Environment.WORKER_BACKEND );
		if ( StringHelper.isEmpty( backend ) || "lucene".equalsIgnoreCase( backend ) ) {
			backendQueueProcessorFactory = new LuceneBackendQueueProcessorFactory();
		}
		else if ( "jms".equalsIgnoreCase( backend ) ) {
			backendQueueProcessorFactory = new JMSBackendQueueProcessorFactory();
		}
		else {
			try {
				Class processorFactoryClass = ReflectHelper.classForName( backend, BatchedQueueingProcessor.class );
				backendQueueProcessorFactory = (BackendQueueProcessorFactory) processorFactoryClass.newInstance();
			}
			catch (ClassNotFoundException e) {
				throw new SearchException( "Unable to find processor class: " + backend, e );
			}
			catch (IllegalAccessException e) {
				throw new SearchException( "Unable to instanciate processor class: " + backend, e );
			}
			catch (InstantiationException e) {
				throw new SearchException( "Unable to instanciate processor class: " + backend, e );
			}
		}
		backendQueueProcessorFactory.initialize( properties, searchFactory );
		searchFactory.setBackendQueueProcessorFactory( backendQueueProcessorFactory );
	}

	public void add(Object entity, Serializable id, WorkType workType, List<Work> queue) {
		//don't check for builder it's done in performWork
		Work work = new Work(entity, id, workType);
		queue.add( work );
	}

	//TODO implements parallel batchWorkers (one per Directory)
	public void performWork(List<Work> queue) {
		int initialSize = queue.size();
		List<LuceneWork> luceneQueue = new ArrayList<LuceneWork>( initialSize ); //TODO load factor for containedIn

		for ( int i = 0 ; i < initialSize ; i++ ) {
			Work work = queue.get( i );
			queue.set( i, null ); // help GC and avoid 2 loaded queues in memory
			Class entityClass = Hibernate.getClass( work.getEntity() );
			DocumentBuilder<Object> builder = searchFactory.getDocumentBuilders().get( entityClass );
			if ( builder == null ) return; //or exception?
			builder.addWorkToQueue(work.getEntity(), work.getId(), work.getType(), luceneQueue, searchFactory);
		}

		Runnable processor = backendQueueProcessorFactory.getProcessor( luceneQueue );
		if ( sync ) {
			processor.run();
		}
		else {
			executorService.execute( processor );
		}
	}

	public void cancelWork(List<Work> queue) {
		queue.clear();
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
		//gracefully stop
		//TODO move to the SF close lifecycle
		if ( executorService != null && !executorService.isShutdown() ) executorService.shutdown();
	}

}
