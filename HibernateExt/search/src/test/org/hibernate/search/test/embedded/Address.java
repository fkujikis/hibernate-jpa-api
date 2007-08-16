//$Id: $
package org.hibernate.search.test.embedded;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.ContainedIn;

/**
 * @author Emmanuel Bernard
 */

@Entity
@Indexed
public class Address {
	@Id
	@GeneratedValue
	@DocumentId
	private Long id;

	@Field(index= Index.TOKENIZED)
	private String street;

	@IndexedEmbedded(depth = 1, prefix = "ownedBy_")
	private Owner ownedBy;

	@OneToOne(mappedBy = "address")
	@ContainedIn
	private Tower tower;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Owner getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Owner ownedBy) {
		this.ownedBy = ownedBy;
	}


	public Tower getTower() {
		return tower;
	}

	public void setTower(Tower tower) {
		this.tower = tower;
	}
}
