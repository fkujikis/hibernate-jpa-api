//$Id: $
package org.hibernate.search.backend;

import java.io.Serializable;

import org.apache.lucene.document.Document;

/**
 * Represent a Serializable Lucene unit work
 *
 * @author Emmanuel Bernard
 */
public abstract class LuceneWork implements Serializable {
	private Document document;
	private Class entityClass;
	private Serializable id;

	public LuceneWork(Serializable id, Class entity) {
		this( id, entity, null );
	}

	public LuceneWork(Serializable id, Class entity, Document document) {
		this.id = id;
		this.entityClass = entity;
		this.document = document;
	}


	public Document getDocument() {
		return document;
	}

	public Class getEntityClass() {
		return entityClass;
	}

	public Serializable getId() {
		return id;
	}
}
