package de.gzockoll.camel.solarmon.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractEntity {

	protected Long id;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public AbstractEntity() {
		super();
	}

	public void setId(Long id) {
		this.id = id;
	}

}