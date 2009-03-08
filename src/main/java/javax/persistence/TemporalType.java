//$Id: TemporalType.java 11171 2007-02-08 03:40:51Z epbernard $
//EJB3 Specification Copyright 2004-2006 Sun Microsystems, Inc.
package javax.persistence;

/**
 * Type used to indicate a specific mapping of Date or Calendar.
 */
public enum TemporalType {
	/**
	 * Map as java.sql.Date
	 */
	DATE,
	/**
	 * Map as java.sql.Time
	 */
	TIME,
	/**
	 * Map as java.sql.Timestamp
	 */
	TIMESTAMP
}