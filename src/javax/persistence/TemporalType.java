//$Id$
//EJB3 Specification Copyright 2004, 2005 Sun Microsystems, Inc.
package javax.persistence;

public enum TemporalType {
	DATE, // java.sql.Date
	TIME, // java.sql.Time
	TIMESTAMP, // java.sql.Timestamp
	NONE
}