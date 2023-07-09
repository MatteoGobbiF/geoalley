package com.mgf.geoalley.service.mapStrategy;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.InvalidCoordinatesException;
import com.mgf.geoalley.exceptions.MapNotFoundException;
import com.mgf.geoalley.exceptions.TitleTakenException;
import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import jakarta.servlet.http.HttpServletRequest;

//Implementazione strategy per editare una mappa non dettagliata
public class MapEditStrategyImpl implements MapEditStrategy {

	//Metodo per mostrare la pagina di editing
	@Override
	public void showMapToEdit(MapEditContext context) {
		
		context.getModel().addAttribute("map", context.getMap());		
	}

	//Metodo per editare la mappa con i nuovi parametri ricevuti
	@Override
	public Map editMap(MapEditContext context)
			throws TitleTakenException, InvalidCoordinatesException, MapNotFoundException, EmptyFieldException {
		
		Map updatedMap;
		
		//Ricerca mappa da modificare sul database
		Optional<Map> mapOptional = context.getMapRepository().findById(context.getId());
		if(!mapOptional.isPresent())
			throw new MapNotFoundException("Map not found");
		Map map = mapOptional.get();
		
		//La descrizione c'è in entrambi i casi
		HttpServletRequest request = context.getRequest();
		String description = request.getParameter("description");
		
		//Se la mappa va trasformata in una mappa dettagliata
		if(context.getTransform()) {
			
			//Parsing dati
			String title = request.getParameter("title");	        
	        int year = Integer.parseInt(request.getParameter("year"));
	        String exactYearValue = request.getParameter("exactYear");
	        double latitude = Double.parseDouble(request.getParameter("latitude"));
	        double longitude = Double.parseDouble(request.getParameter("longitude"));
	        boolean exactYear = false;
	        if (exactYearValue != null)
	            exactYear = true;
	        
	        //Controlli validità dati e unicità titolo
	        if(title.isBlank() || title==null)
	        	throw new EmptyFieldException("Title can't be null");
	        if(latitude>180 || latitude<-180 || longitude > 180 || longitude<-180)
	        	throw new InvalidCoordinatesException("Invalid coordinates");	        
			if(context.getMapRepository().findByTitle(title).isPresent())
				throw new TitleTakenException("There is already a map with such title");
			
			//Creazione nuova mappa
			DetailedMap newMap = new DetailedMap(title, map.getUrl(), description, map.getUser(), Year.of(year), exactYear, latitude, longitude, context.getTags());
			
			//Salvataggio nuova mappa e eliminazione mappa vecchia (Il file rimane inalterato)
			updatedMap = context.getMapRepository().save(newMap);
			context.getMapRepository().deleteById(map.getId());
			
			//Aggiunta dei tag sul database (Se presenti)
			List<MapTag> newTags = context.getTags();					
			for (MapTag newTag : newTags) {
	
				if(newTag.getTagName()!=null && !newTag.getTagName().isEmpty()) {
					newTag.setMap((DetailedMap)updatedMap);
					context.getTagRepository().save(newTag);
				}
			}

		//Se invece la mappa rimane non dettagliata si aggiorna solo la descrizione
		}else {
			map.setDescription(description);
			updatedMap = context.getMapRepository().save(map);
		}
		
        return updatedMap;		
	}

}
