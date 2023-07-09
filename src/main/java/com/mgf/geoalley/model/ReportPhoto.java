package com.mgf.geoalley.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="report_photo")
public class ReportPhoto {

	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String description;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="photo_id")
	private Photo photo;
	
	private boolean open;

	public ReportPhoto(String description, Photo photo) {
		this.description = description;
		this.photo = photo;
		this.open=true; //Un report appena creato è aperto
	}
	
	public ReportPhoto() {
		
	}

	//Getter e setter, il setter di open si chiama close() e chiude il report. I report non possono essere riaperti.
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void close() {
		this.open=false;
	}
}

