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
@Table(name="comment")
public class Comment implements HasUser{
	
	//Id autogenerato dal database con auto-increment
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String content;
	private LocalDateTime createdAt;
	
	@ManyToOne
	@JoinColumn(name="map_id")
	private Map map;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public Comment(User user, Map map, String content) {
	    this.user = user;
	    this.map = map;
	    this.content = content;
	    //createdAt non viene impostato automaticamente
	    this.createdAt = LocalDateTime.now();
	}
	
	public Comment() {
		
	}

	//Getters e setters, manca setCreatedAt volutamente perché il suo valore è stabilito alla creazione
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime dateTime() {
		return createdAt;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
