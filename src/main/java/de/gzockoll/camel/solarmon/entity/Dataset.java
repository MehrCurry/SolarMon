package de.gzockoll.camel.solarmon.entity;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Dataset extends AbstractEntity {

	private double tagesEnergie;
	private double momentanLeistung;
	private double jahresEnergie;
	private final Date created=new Date();
	
	public Dataset() {
	}

	public double getTagesEnergie() {
		return tagesEnergie;
	}

	public double getMomentanLeistung() {
		return momentanLeistung;
	}

	public double getJahresEnergie() {
		return jahresEnergie;
	}

	public void setTagesEnergie(double tagesEnergie) {
		this.tagesEnergie = tagesEnergie;
	}

	public void setMomentanLeistung(double momentanLeistung) {
		this.momentanLeistung = momentanLeistung;
	}

	public void setJahresEnergie(double jahresEnergie) {
		this.jahresEnergie = jahresEnergie;
	}
	
	
}
