//$Id: $
package org.hibernate.search.backend.impl;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.hibernate.search.backend.QueueingProcessor;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.backend.Work;
import org.hibernate.search.util.WeakIdentityHashMap;

/**
 * Execute some work inside a transaction sychronization
 *
 * @author Emmanuel Bernard
 */
public class PostTransactionWorkQueueSynchronization implements Synchronization {
	private QueueingProcessor queueingProcessor;
	private boolean consumed;
	private WeakIdentityHashMap queuePerTransaction;
	private List<Work> queue = new ArrayList<Work>();

	/**
	 * in transaction work
	 */
	public PostTransactionWorkQueueSynchronization(QueueingProcessor queueingProcessor, WeakIdentityHashMap queuePerTransaction) {
		this.queueingProcessor = queueingProcessor;
		this.queuePerTransaction = queuePerTransaction;
	}

	public void add(Object entity, Serializable id, WorkType workType) {
		queueingProcessor.add( entity, id, workType, queue );
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void beforeCompletion() {
	}

	public void afterCompletion(int i) {
		try {
			if ( Status.STATUS_COMMITTED == i ) {
				queueingProcessor.performWork(queue);
			}
			else {
				queueingProcessor.cancelWork(queue);
			}
		}
		finally {
			consumed = true;
			//clean the Synchronization per Transaction
			//not needed stricto sensus but a cleaner approach and faster than the GC
			if (queuePerTransaction != null) queuePerTransaction.removeValue( this ); 
		}
	}
}
