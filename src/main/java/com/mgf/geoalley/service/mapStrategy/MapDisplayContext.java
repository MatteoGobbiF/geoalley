package com.mgf.geoalley.service.mapStrategy;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.Map;

public class MapDisplayContext {

	private MapDisplayStrategy displayStrategy;
	
	public MapDisplayContext(MapDisplayStrategy displayStrategy) {
		this.displayStrategy=displayStrategy;
	}
	
	public void setDisplayStrategy(MapDisplayStrategy displayStrategy) {
		this.displayStrategy=displayStrategy;
	}
	
	public void displayMap(Map map, Model model, HttpServletRequest request) throws UserNotLoggedException{
		displayStrategy.display(map, model, request);
	}
		
}
