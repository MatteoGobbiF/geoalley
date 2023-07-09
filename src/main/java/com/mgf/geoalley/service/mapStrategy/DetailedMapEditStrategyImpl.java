package com.mgf.geoalley.service.mapStrategy;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import jakarta.servlet.http.HttpServletRequest;

//Implementazione strategy per editare una mappa dettagliata
public class DetailedMapEditStrategyImpl implements MapEditStrategy {

	//Metodo per mostrare la pagina di editing
	@Override
	public void showMapToEdit(MapEditContext context) {
			
		context.getModel().addAttribute("map", context.getMap());
		
		//Se la mappa ha dei tag si scrivono in una stringa per poter precompilare l'input field nel form
		Optional<List<MapTag>> tags = context.getTagRepository().findByMap(context.getMap());
		if (tags.isPresent()) {
			StringBuilder tagNames = new StringBuilder();
			for (MapTag tag : tags.get()) {
			    tagNames.append(tag.getTagName()).append(", ");
			}
			//Rimuove l'ultima virgola espazio
			if (tagNames.length() > 0) {
			    tagNames.delete(tagNames.length() - 2, tagNames.length());
			}
		 	context.getModel().addAttribute("tags", tagNames.toString());
		}				
	}

	//Metodo per editare la mappa con i nuovi parametri ricevuti
	@Override
	public DetailedMap editMap(MapEditContext context) throws TitleTakenException, InvalidCoordinatesException, MapNotFoundException, EmptyFieldException {
		
		//Parsing parametri dalla richiesta
		HttpServletRequest request = context.getRequest();
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		int year = Integer.parseInt(request.getParameter("year"));
		String exactYearValue = request.getParameter("exactYear");
		double latitude = Double.parseDouble(request.getParameter("latitude"));
		double longitude = Double.parseDouble(request.getParameter("longitude"));
		boolean exactYear = false;
		if (exactYearValue != null)
			exactYear = true;
		
		//Controllo validità dati
		if(title.isBlank() || title==null)
		    	throw new EmptyFieldException("Title can't be null");
		if(latitude>180 || latitude<-180 || longitude > 180 || longitude<-180)
			throw new InvalidCoordinatesException("Invalid coordinates");
		
		//Ricerca mappa su database
		Optional<Map> mapOptional = context.getMapRepository().findById(context.getId());
		if(!mapOptional.isPresent())
			throw new MapNotFoundException("Map not found");
		DetailedMap map = (DetailedMap) mapOptional.get();
		
		//Controllo unicità titolo se cambiato
		if(!map.getTitle().equals(title) && context.getMapRepository().findByTitle(title).isPresent())
			throw new TitleTakenException("There is already a map with such title");
		
		//Modifica dati della mappa
		map.setTitle(title);
		map.setDescription(description);
		map.setYear(Year.of(year));
		map.setExactYear(exactYear);
		map.setLatitude(latitude);
		map.setLongitude(longitude);
		
		List<MapTag> newTags = context.getTags();		
		
		//Salvataggio mappa aggiornata su database
		DetailedMap updatedMap = context.getMapRepository().save(map);
		
		Optional<List<MapTag>> existingTagsOptional = context.getTagRepository().findByMap(updatedMap);
		
		//Cancellazione tag esistenti e salvataggio nuovi tag
		if(existingTagsOptional.isPresent()) {
			
			List<MapTag> existingTags = existingTagsOptional.get();
			for (MapTag existingTag : existingTags)
				    context.getTagRepository().delete(existingTag);
		}
		
		for (MapTag newTag : newTags) {
		
			if(newTag.getTagName()!=null && !newTag.getTagName().isEmpty()) {
				newTag.setMap(updatedMap);
				context.getTagRepository().save(newTag);
			}
		}
		
		return updatedMap;

	}

}
