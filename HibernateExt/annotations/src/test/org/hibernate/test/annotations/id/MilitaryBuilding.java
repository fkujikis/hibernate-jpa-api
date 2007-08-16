//$Id$
package org.hibernate.test.annotations.id;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;

/**
 * @author Emmanuel Bernard
 */
@MappedSuperclass
@IdClass(Location.class)
public class MilitaryBuilding {
	@Id
	public double longitude;
	@Id
	public double latitude;
}
