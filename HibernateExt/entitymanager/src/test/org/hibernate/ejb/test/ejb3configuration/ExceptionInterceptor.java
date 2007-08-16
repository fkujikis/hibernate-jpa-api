//$Id$
package org.hibernate.ejb.test.ejb3configuration;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * @author Emmanuel Bernard
 */
public class ExceptionInterceptor implements Interceptor {
	public static final String EXCEPTION_MESSAGE = "Interceptor enabled";

	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
			throws CallbackException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean onFlushDirty(
			Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
			Type[] types
	) throws CallbackException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
			throws CallbackException {
		throw new IllegalStateException( EXCEPTION_MESSAGE );
	}

	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
			throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void preFlush(Iterator entities) throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void postFlush(Iterator entities) throws CallbackException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public Boolean isTransient(Object entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public int[] findDirty(
			Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
			Type[] types
	) {
		return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public String getEntityName(Object object) throws CallbackException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Object getEntity(String entityName, Serializable id) throws CallbackException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void afterTransactionBegin(Transaction tx) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void beforeTransactionCompletion(Transaction tx) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void afterTransactionCompletion(Transaction tx) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public String onPrepareStatement(String sql) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
