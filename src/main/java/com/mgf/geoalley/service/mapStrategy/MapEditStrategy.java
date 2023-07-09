package com.mgf.geoalley.service.mapStrategy;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.InvalidCoordinatesException;
import com.mgf.geoalley.exceptions.MapNotFoundException;
import com.mgf.geoalley.exceptions.TitleTakenException;
import com.mgf.geoalley.model.Map;


public interface MapEditStrategy {
	
	public void showMapToEdit(MapEditContext context);
 
	public Map editMap(MapEditContext context) throws TitleTakenException, InvalidCoordinatesException, MapNotFoundException, EmptyFieldException;;
}
