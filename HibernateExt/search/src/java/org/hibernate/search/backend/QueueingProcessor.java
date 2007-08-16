//$Id: $
package org.hibernate.search.backend;

import java.util.List;
import java.io.Serializable;

import org.hibernate.search.backend.LuceneWork;

/**
 * Pile work operations
 * No thread safety has to be implemented, the queue being scoped already
 * The implementation must be "stateless" wrt the queue through (ie not store the queue state)
 *
 * @author Emmanuel Bernard
 */
public interface QueueingProcessor {
	/**
	 * Add a work
	 * TODO move that womewhere else, it does not really fit here
	 */
	void add(Object entity, Serializable id, WorkType workType, List<Work> queue);

	/**
	 * Execute works
	 * @param queue
	 */
	void performWork(List<Work> queue);

	/**
	 * Rollback works
	 * @param queue
	 */
	void cancelWork(List<Work> queue);
}
