//$Id$
package org.hibernate.test.annotations.cid;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Entity having a many to one in its pk
 *
 * @author Emmanuel Bernard
 */
@Entity
public class Child {
	@EmbeddedId
	@AttributeOverride(name = "nthChild", column = @Column(name = "nth"))
	public ChildPk id;
}
