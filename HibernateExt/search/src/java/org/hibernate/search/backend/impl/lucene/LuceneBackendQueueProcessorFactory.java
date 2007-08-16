//$Id: $
package org.hibernate.search.backend.impl.lucene;

import java.util.Properties;
import java.util.List;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.BackendQueueProcessorFactory;
import org.hibernate.search.backend.LuceneWork;

/**
 * @author Emmanuel Bernard
 */
public class LuceneBackendQueueProcessorFactory implements BackendQueueProcessorFactory {
	private SearchFactory searchFactory;

	public void initialize(Properties props, SearchFactory searchFactory) {
		this.searchFactory = searchFactory;
	}

	public Runnable getProcessor(List<LuceneWork> queue) {
		return new LuceneBackendQueueProcessor( queue, searchFactory );
	}
}
