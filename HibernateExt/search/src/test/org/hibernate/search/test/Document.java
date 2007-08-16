//$Id: Document.java 10742 2006-11-07 01:03:16Z epbernard $
package org.hibernate.search.test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed(index = "Documents")
public class Document {
	private Long id;
	private String title;
	private String summary;
	private String text;

	Document() {
	}

	public Document(String title, String summary, String text) {
		super();
		this.summary = summary;
		this.text = text;
		this.title = title;
	}

	@Id
	@GeneratedValue
	//@Keyword(id = true)
    @DocumentId
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//@Text
    @Field( store = Store.YES, index = Index.TOKENIZED )
    @Boost(2)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	//@Unstored(name = "Abstract")
    @Field( name="Abstract", store = Store.NO, index = Index.TOKENIZED )
    public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Lob
	//@Unstored
    @Field( store = Store.NO, index = Index.TOKENIZED )
    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
