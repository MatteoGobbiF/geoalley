package com.mgf.geoalley.service.mapStrategy;

import java.util.List;

import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.InvalidCoordinatesException;
import com.mgf.geoalley.exceptions.MapNotFoundException;
import com.mgf.geoalley.exceptions.TitleTakenException;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import com.mgf.geoalley.repository.MapRepository;
import com.mgf.geoalley.repository.TagRepository;

import jakarta.servlet.http.HttpServletRequest;

//Context con tutti i possibili attributi per le varie circostanze e strategy utilizzate
public class MapEditContext {
	
	private MapEditStrategy editStrategy;
	private Integer id;
	private Map map;
	private MapRepository mapRepository;
	private TagRepository tagRepository;
	private List<MapTag> tags;
	private Model model;
	private HttpServletRequest request;
	//Attributo che indica se una mappa non dettagliata deve essere trasformata in una dettagliata
	private boolean transform = false;
	
	//Quattro costruttori, uno per ogni situazione in cui viene utilizzato il context
	public MapEditContext(Map map, Model model) {
		super();
		this.map = map;
		this.model = model;
	}

	public MapEditContext(Map map, Model model, TagRepository tagRepository) {
		super();
		this.map = map;
		this.tagRepository = tagRepository;
		this.model = model;
	}

	public MapEditContext(Integer id, MapRepository mapRepository, TagRepository tagRepository, List<MapTag> tags,
			HttpServletRequest request) {
		super();
		this.id = id;
		this.mapRepository = mapRepository;
		this.tagRepository = tagRepository;
		this.tags = tags;
		this.request = request;
	}

	public MapEditContext(Integer id, MapRepository mapRepository, HttpServletRequest request) {
		super();
		this.id = id;
		this.mapRepository = mapRepository;
		this.request = request;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MapRepository getMapRepository() {
		return mapRepository;
	}

	public void setMapRepository(MapRepository mapRepository) {
		this.mapRepository = mapRepository;
	}

	public TagRepository getTagRepository() {
		return tagRepository;
	}

	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	public List<MapTag> getTags() {
		return tags;
	}

	public void setTags(List<MapTag> tags) {
		this.tags = tags;
	}
	
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}	
	
	public boolean getTransform() {
		return transform;
	}
	
	public void setTransform(boolean transform) {
		this.transform = transform;
	}
		
	public void setStrategy(MapEditStrategy strategy) {
		this.editStrategy=strategy;
	}
	
	
	
	public Map editMap() throws TitleTakenException, InvalidCoordinatesException, MapNotFoundException, EmptyFieldException {
		return this.editStrategy.editMap(this);
	}

	public void showMapToEdit() {
		this.editStrategy.showMapToEdit(this);
	}
	
		
		
}
