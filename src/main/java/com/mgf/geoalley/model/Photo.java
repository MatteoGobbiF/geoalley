package com.mgf.geoalley.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="photo")
public class Photo implements HasUser{
	
	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String url;
	private String description;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToMany(mappedBy="photo", cascade = CascadeType.REMOVE)
	private List<ReportPhoto> reports;
	
	@OneToMany(mappedBy="photo", cascade = CascadeType.REMOVE)
	private List<PhotoGuess> guesses;

	public Photo(String url, String description, User user) {
		this.url = url;
		this.description = description;
		this.user = user;
	}
	
	public Photo() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<PhotoGuess> getGuesses() {
		return guesses;
	}

	public void setGuesses(List<PhotoGuess> guesses) {
		this.guesses = guesses;
	}
	
}
