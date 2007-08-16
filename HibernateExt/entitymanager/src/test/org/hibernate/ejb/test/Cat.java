//$Id$
package org.hibernate.ejb.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.Min;

/**
 * @author Emmanuel Bernard
 */
@Entity
@EntityListeners(LastUpdateListener.class)
public class Cat implements Serializable {
	private Integer id;
	private String name;
	private Date dateOfBirth;
	private int age;
	private long length;
	private Date lastUpdate;
	private int manualVersion = 0;
	private int postVersion = 0;
	private static final List ids = new ArrayList();

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Length(min = 4)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int getManualVersion() {
		return manualVersion;
	}

	public void setManualVersion(int manualVersion) {
		this.manualVersion = manualVersion;
	}

	@Transient
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getPostVersion() {
		return postVersion;
	}

	public void setPostVersion(int postVersion) {
		this.postVersion = postVersion;
	}

	@PostUpdate
	private void someLateUpdateWorking() {
		this.postVersion++;
	}

	@PostLoad
	public void calculateAge() {
		Calendar birth = new GregorianCalendar();
		birth.setTime( dateOfBirth );
		Calendar now = new GregorianCalendar();
		now.setTime( new Date() );
		int adjust = 0;
		if ( now.get( Calendar.DAY_OF_YEAR ) - birth.get( Calendar.DAY_OF_YEAR ) < 0 ) {
			adjust = -1;
		}
		age = now.get( Calendar.YEAR ) - birth.get( Calendar.YEAR ) + adjust;
	}

	@PostPersist
	public synchronized void addIdsToList() {
		ids.add( getId() );
	}

	public static synchronized List getIdList() {
		return Collections.unmodifiableList( ids );
	}

	@Min(0)
	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
}
