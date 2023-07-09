package com.mgf.geoalley.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="photo_guess")
public class PhotoGuess implements HasUser{

	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String comment;
	private LocalDateTime createdAt;
	private double latitude;
	private double longitude;
	
	@ManyToOne
	@JoinColumn(name="photo_id")
	private Photo photo;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public PhotoGuess(String comment, Photo photo, User user, double latitude, double longitude) {
		this.comment = comment;
		this.photo = photo;
		this.user = user;
		this.createdAt = LocalDateTime.now();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public PhotoGuess() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	
}
