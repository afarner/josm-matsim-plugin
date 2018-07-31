package org.matsim.contrib.josm.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@SuppressWarnings("restriction")
public class LinkPreset {
	private StringProperty name = new SimpleStringProperty();
	private StringProperty freespeed = new SimpleStringProperty();
	private StringProperty capacity = new SimpleStringProperty();
	private StringProperty permlanes = new SimpleStringProperty();
	private StringProperty mode = new SimpleStringProperty();
	private StringProperty oneway = new SimpleStringProperty();

	public LinkPreset(String name, String freespeed, String capacity, String permlanes, String mode, String oneway) {
		this.name.set(name);
		this.freespeed.set(freespeed);
		this.capacity.set(capacity);
		this.permlanes.set(permlanes);
		this.mode.set(mode);
		this.oneway.set(oneway);

	}

	public StringProperty nameProperty() {
		return name;
	}
	
	public void setNameProperty(String name) {
		this.name.set(name);
	}
	
	public StringProperty freespeedProperty() {
		return freespeed;
	}
	
	public void setFreespeedProperty(String freespeed) {
		this.freespeed.set(freespeed);
	}
	
	public StringProperty capacityProperty() {
		return capacity;
	}
	
	public void setCapacityProperty(String capacity) {
		this.capacity.set(capacity);
	}
	
	public StringProperty permlanesProperty() {
		return permlanes;
	}
	
	public void setPermlanesProperty(String permlanes) {
		this.permlanes.set(permlanes);
	}
	
	public StringProperty modeProperty() {
		return mode;
	}
	
	public void setModeProperty(String mode) {
		this.mode.set(mode);
	}
	
	public StringProperty onewayProperty() {
		return oneway;
	}
	
	public void setOnewayProperty(String oneway) {
		this.oneway.set(oneway);
	}
}
