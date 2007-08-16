package org.hibernate.cfg.reveng;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.exception.SQLExceptionConverter;

/**
 * Provides runtime-only information for reverse engineering process.
 * e.g. current connection provider, exception converter etc. 
 * 
 * @author max
 *
 */
public class ReverseEngineeringRuntimeInfo {

	private final ConnectionProvider connectionProvider;
	private final SQLExceptionConverter SQLExceptionConverter;
	
	protected ReverseEngineeringRuntimeInfo(ConnectionProvider provider, SQLExceptionConverter sec, DatabaseCollector dbs) {
		this.connectionProvider = provider;
		this.SQLExceptionConverter = sec;	
	}
	
	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	
	public SQLExceptionConverter getSQLExceptionConverter() {
		return SQLExceptionConverter;
	}
	
	
		
}
