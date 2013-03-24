package de.gzockoll.camel.solarmon.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Customer extends AbstractEntity {

	private String name;

	private final Date created = new Date();

	public Customer() {
	}
	
	public Customer(String name) {
		super();
		this.name = name;
	}
}
