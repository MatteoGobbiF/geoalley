package com.mgf.geoalley.model;

import java.time.Year;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//In relazione 1 a 1 con Map automaticamente grazie all'ereditarietà e JPA
@Entity
@Table(name="map_details")
public class DetailedMap extends Map{

	private String title;
	private Year year;
	private boolean exactYear;
	private double latitude;
	private double longitude;
	
	@OneToMany(mappedBy="map", cascade = CascadeType.REMOVE)
	private List<MapTag> tags;

	public DetailedMap() {
		
	}
	
	public DetailedMap(String title, String url, String description, User user, Year year, boolean exactYear, double latitude, double longitude, List<MapTag> tags) {
		super(url, description, user);
		this.title=title;
		this.year=year;
		this.exactYear=exactYear;
		this.latitude=latitude;
		this.longitude=longitude;
		this.tags=tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public boolean isExactYear() {
		return exactYear;
	}

	public void setExactYear(boolean exactYear) {
		this.exactYear = exactYear;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<MapTag> getTags() {
		return tags;
	}

	public void setTags(List<MapTag> tags) {
		this.tags = tags;
	}
	
	public String toString() {
		return title + " " + super.getDescription();
	}
	
	
	
}
