//$Id: $
package org.hibernate.search.backend.impl.lucene;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.Workspace;

/**
 * Apply the operations to Lucene directories
 * avoiding deadlocks
 *
 * @author Emmanuel Bernard
 */
public class LuceneBackendQueueProcessor implements Runnable {
	private List<LuceneWork> queue;
	private SearchFactory searchFactory;

	public LuceneBackendQueueProcessor(List<LuceneWork> queue, SearchFactory searchFactory) {
		this.queue = queue;
		this.searchFactory = searchFactory;
	}

	public void run() {
		Workspace workspace;
		LuceneWorker worker;
		workspace = new Workspace( searchFactory );
		worker = new LuceneWorker( workspace );
		try {
			deadlockFreeQueue(queue, workspace);
			for ( LuceneWork luceneWork : queue ) {
				worker.performWork( luceneWork );
			}
		}
		finally {
			workspace.clean();
			queue.clear();
		}
	}

	/**
	 * one must lock the directory providers in the exact same order to avoid
	 * dead lock between concurrent threads or processes
	 * To achieve that, the work will be done per directory provider
	 */
	private void deadlockFreeQueue(List<LuceneWork> queue, final Workspace workspace) {
		Collections.sort( queue, new Comparator<LuceneWork>() {
			public int compare(LuceneWork o1, LuceneWork o2) {
				long h1 = getWorkHashCode( o1, workspace );
				long h2 = getWorkHashCode( o2, workspace );
				return h1 < h2 ?
						-1 :
						h1 == h2 ?
							0 :
							1;
			}
		} );
	}

	private long getWorkHashCode(LuceneWork luceneWork, Workspace workspace) {
		long h = workspace.getDocumentBuilder( luceneWork.getEntityClass() ).hashCode() * 2;
		if ( luceneWork instanceof AddLuceneWork ) h+=1; //addwork after deleteWork
		return h;
	}
}
