package com.mgf.geoalley.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="map_tag")
public class MapTag {

	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String tagName;
	
	@ManyToOne
	@JoinColumn(name="map_id")
	private DetailedMap map;
	
	public MapTag(String tagName) {
		this.tagName = tagName;
	}
	
	public MapTag(String tagName, DetailedMap map) {
		this.tagName = tagName;
		this.map = map;
	}
	
	public MapTag() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public DetailedMap getMap() {
		return map;
	}

	public void setMap(DetailedMap map) {
		this.map = map;
	}
	
	@Override
	public String toString() {
		return tagName;
	}
	
	
	
}
