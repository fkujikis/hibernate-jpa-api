//$Id$
package org.hibernate.test.annotations.access;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class BigBed extends Bed {
	public int size;
}
