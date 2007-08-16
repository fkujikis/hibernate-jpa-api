//$Id: $
package org.hibernate.search.backend.impl;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.hibernate.search.backend.Worker;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.QueueingProcessor;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.impl.BatchedQueueingProcessor;
import org.hibernate.search.util.WeakIdentityHashMap;
import org.hibernate.search.SearchFactory;
import org.hibernate.event.EventSource;
import org.hibernate.Transaction;

/**
 * Queue works per transaction.
 * If out of transaction, the work is executed right away
 *
 * When <code>hibernate.search.worker.type</code> is set to <code>async</code>
 * the work is done in a  
 *
 * @author Emmanuel Bernard
 */
public class TransactionalWorker implements Worker {
	//not a synchronized map since for a given transaction, we have not concurrent access
	protected WeakIdentityHashMap synchronizationPerTransaction = new WeakIdentityHashMap();
	private QueueingProcessor queueingProcessor;

	public void performWork(Object entity, Serializable id, WorkType workType, EventSource session) {
		if ( session.isTransactionInProgress() ) {
			Transaction transaction = session.getTransaction();
			PostTransactionWorkQueueSynchronization txSync = (PostTransactionWorkQueueSynchronization)
					synchronizationPerTransaction.get( transaction );
			if ( txSync == null || txSync.isConsumed() ) {
				txSync = new PostTransactionWorkQueueSynchronization( queueingProcessor, synchronizationPerTransaction );
				transaction.registerSynchronization( txSync );
				synchronizationPerTransaction.put(transaction, txSync);
			}
			txSync.add( entity, id, workType );
		}
		else {
			List<Work> queue = new ArrayList<Work>(2); //one work can be split
			queueingProcessor.add( entity, id, workType, queue );
			queueingProcessor.performWork( queue );
		}
	}

	public void initialize(Properties props, SearchFactory searchFactory) {
		this.queueingProcessor = new BatchedQueueingProcessor( searchFactory, props );
	}
}
