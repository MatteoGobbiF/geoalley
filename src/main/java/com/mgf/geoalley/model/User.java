package com.mgf.geoalley.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="user", uniqueConstraints= @UniqueConstraint(columnNames= {"email", "username"}))
public class User {
	
	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String username;
	private String email;
	private String password;
	private boolean admin;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Map> maps;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<Photo> photos;
	
	
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.admin=false;			//Tutti gli utenti creati non dalla console del database non possono essere admin
	}
	public User() {
		
	}

	//Getters e setters, manca volutamente il setter di admin e il suo getter si chiama isAdmin() 
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Set<Map> getMaps() {
		return maps;
	}
	public void setMaps(Set<Map> maps) {
		this.maps = maps;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public Set<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(Set<Photo> photos) {
		this.photos = photos;
	}
	
	
	
	
}
