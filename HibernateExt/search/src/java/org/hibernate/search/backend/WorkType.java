//$Id: $
package org.hibernate.search.backend;

/**
 * @author Emmanuel Bernard
 */
public enum WorkType {
	ADD,
	UPDATE,
	DELETE
	//add INDEX at some point to behave differently during the queue process?
}
