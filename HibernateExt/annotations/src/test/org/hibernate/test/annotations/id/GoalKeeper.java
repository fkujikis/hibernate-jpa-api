//$Id$
package org.hibernate.test.annotations.id;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class GoalKeeper extends Footballer {
	public GoalKeeper() {
	}

	public GoalKeeper(String firstname, String lastname, String club) {
		super( firstname, lastname, club );
	}
}
