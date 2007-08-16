//$Id: Environment.java 10742 2006-11-07 01:03:16Z epbernard $
package org.hibernate.search;

/**
 * @author Emmanuel Bernard
 */
public final class Environment {
	/**
	 * Enable listeners auto registration in Hibernate Annotations and EntityManager. Default to true.
	 */
	public static final String AUTOREGISTER_LISTENERS = "hibernate.search.autoregister_listeners";
	/**
	 * Indexes base directory
	 */
	public static final String INDEX_BASE_DIR = "hibernate.search.index_dir";

	/**
	 * Lucene analyser
	 */
	public static final String ANALYZER_CLASS = "hibernate.search.analyzer";

	public static final String WORKER_PREFIX = "hibernate.search.worker.";
	public static final String WORKER_SCOPE = WORKER_PREFIX + "scope";
	public static final String WORKER_BACKEND = WORKER_PREFIX + "backend";
	public static final String WORKER_EXECUTION = WORKER_PREFIX + "execution";
	/**
	 * only used then execution is async
	 * Thread pool size
	 * default 1
	 */
	public static final String WORKER_THREADPOOL_SIZE = Environment.WORKER_PREFIX + "thread_pool.size";
	/**
	 * only used then execution is async
	 * Size of the buffer queue (besides the thread pool size)
	 * default infinite
	 */
	public static final String WORKER_WORKQUEUE_SIZE = Environment.WORKER_PREFIX + "buffer_queue.max";
}
