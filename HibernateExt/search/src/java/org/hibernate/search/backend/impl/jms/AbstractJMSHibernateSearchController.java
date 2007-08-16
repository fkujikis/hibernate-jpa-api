//$Id: $
package org.hibernate.search.backend.impl.jms;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.util.ContextHelper;

/**
 * Implement the Hibernate Search controller responsible for processing the
 * work send through JMS by the slave nodes.
 *
 * Note the subclass implementation has to implements javax.jms.MessageListener
 * //TODO Ask Bill why it is required
 *
 * @author Emmanuel Bernard
 */
public abstract class AbstractJMSHibernateSearchController implements MessageListener {
	private static Log log = LogFactory.getLog( AbstractJMSHibernateSearchController.class );

	/**
	 * return the current or give a new session
	 * This session is not used per se, but is the link to access the Search configuration
	 *
	 * A typical EJB 3.0 usecase would be to get the session from the container (injected)
	 * eg in JBoss EJB 3.0
	 * <code>
	 * @PersistenceContext private Session session;
	 *
	 * protected Session getSession() {
	 *     return session
	 * }
	 *
	 * eg in any container
	 * <code>
	 * @PersistenceContext private EntityManager entityManager;
	 *
	 * protected Session getSession() {
	 *     return (Session) entityManager.getdelegate();
	 * }
	 */
	protected abstract Session getSession();

	/**
	 * Ensure to clean the resources after use.
	 * If the session has been directly or indirectly injected, this method is empty
	 */
	protected abstract void cleanSessionIfNeeded(Session session);

	/**
	 * Process the Hibernate Search work queues received
	 */
	public void onMessage(Message message) {
		if ( !( message instanceof ObjectMessage ) ) {
			log.error( "Incorrect message type: " + message.getClass() );
			return;
		}
		ObjectMessage objectMessage = (ObjectMessage) message;
		List<LuceneWork> queue;
		try {
			queue = (List<LuceneWork>) objectMessage.getObject();
		}
		catch (JMSException e) {
			log.error( "Unable to retrieve object from message: " + message.getClass(), e );
			return;
		}
		catch (ClassCastException e) {
			log.error( "Illegal object retrieved from message", e );
			return;
		}
		Runnable worker = getWorker( queue );
		worker.run();
	}

	private Runnable getWorker(List<LuceneWork> queue) {
		//FIXME casting sucks becasue we do not control what get session from
		Session session = getSession();
		Runnable processor = null;

		try {
			SearchFactory factory = ContextHelper.getSearchFactory( session );
			processor = factory.getBackendQueueProcessorFactory().getProcessor( queue );
		}
		finally {
			cleanSessionIfNeeded(session);
		}
		return processor;
	}

	@PostConstruct
	public void initialize() {
		//init the source copy process
		//TODO actually this is probably wrong since this is now part of the DP
	}

	@PreDestroy
	public void shutdown() {
		//stop the source copy process
		//TODO actually this is probably wrong since this is now part of the DP
	}
}
