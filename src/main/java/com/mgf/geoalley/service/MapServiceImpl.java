package com.mgf.geoalley.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.MapRepository;
import com.mgf.geoalley.repository.TagRepository;
import com.mgf.geoalley.service.mapStrategy.DetailedMapDisplayStrategyImpl;
import com.mgf.geoalley.service.mapStrategy.DetailedMapEditStrategyImpl;
import com.mgf.geoalley.service.mapStrategy.MapDisplayStrategy;
import com.mgf.geoalley.service.mapStrategy.MapDisplayStrategyImpl;
import com.mgf.geoalley.service.mapStrategy.MapEditContext;
import com.mgf.geoalley.service.mapStrategy.MapEditStrategyImpl;
import com.mgf.geoalley.utils.SecurityChecks;


@Service
public class MapServiceImpl implements MapService {
	

	private final MapRepository mapRepository;
	private final TagRepository tagRepository;
	
	@Autowired
	public MapServiceImpl(MapRepository mapRepository, TagRepository tagRepository) {
		this.mapRepository = mapRepository;
		this.tagRepository = tagRepository;
	}
	
	private final Path rootLocation = Paths.get("src/main/resources/static");

	//Metodo che fa il parsing di una stringa di tag e crea una lista di MapTag
	@Override
	public List<MapTag> parseTags(String tags) {
		List<MapTag> mapTags = new ArrayList<>();
		for(String tag:tags.split(","))
			mapTags.add(new MapTag(tag.trim()));
		return mapTags;
	}

	//Metodo che salva in memoria il file caricato
	@Override
	public void store(MultipartFile file, String fileName) throws StorageException {
		
		try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
        } catch (Exception e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename());
        }
		
	}

	//Metodo che crea una nuova mappa dettagliata (I controlli dei dati sono fatti nel controller)
	@Override
	public DetailedMap createNewDetailedMap(String title, String mapTags, String description, int year,
			boolean exactYear, double latitude, double longitude, MultipartFile file, User user) throws StorageException, TitleTakenException {
		
		//Preparazione dati nel formato giusto
		List<MapTag> tags = parseTags(mapTags);
		Year formattedYear = Year.of(year);
	 
		//Controllo esistenza mappa con lo stesso titolo
		if(mapRepository.findByTitle(title).isPresent())
			throw new TitleTakenException("There is already a map with such title");
		
		//Creazione nome file per avere nome unico
		String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		
		//Salvataggio file in memoria
		store(file, uniqueFileName);
		
		//Creazione e memorizzazione mappa nel database
		DetailedMap detailedMap = new DetailedMap(title, uniqueFileName, description, user, formattedYear, exactYear, latitude, longitude, tags);
		DetailedMap createdMap = mapRepository.save(detailedMap);
		
		//Memorizzazione dei tag corrispondenti nel database
		for (MapTag tag : tags) {
			if(!tag.getTagName().isEmpty()) {
				tag.setMap(createdMap);
				tagRepository.save(tag);
			}
		}
		
		return createdMap;
		
	}

	//Metodo che prepara i model attributes per mostrare la mappa e i suoi dati
	//Ritorna un valore booleano per dire al controller a quale template rimandare l'utente
	//Viene impiegato il pattern Stratgy
	@Override
	public boolean showMap(Integer id, Model model, HttpServletRequest request) throws MapNotFoundException, UserNotLoggedException {
		
		Optional<Map> mapOptional = mapRepository.findById(id);
		boolean showDetailed = false;
		
		//In base al tipo di mappa trovata crea la displaystrategy appropriata
		if(mapOptional.isPresent()) {
			Map map = mapOptional.get();
			MapDisplayStrategy displayStrategy;
			if(map instanceof DetailedMap) {
				displayStrategy	= new DetailedMapDisplayStrategyImpl(tagRepository);
				showDetailed = true;
			}
			else
				displayStrategy = new MapDisplayStrategyImpl();
			
			displayStrategy.display(map, model, request);
			return showDetailed;
		}
		else
			throw new MapNotFoundException("Map not found with id " + id);
		
		
	}

	//Metodo che prepara la pagina per apportare modifiche
	//Ritorna un valore che indica al controller a quale template indirazzare l'utente
	//Viene impiegato il pattern strategy
	@Override
	public boolean showEditMap(Map map, Model model, HttpServletRequest request) throws MapNotFoundException, UserNotLoggedException, UserUnauthorizedException {
		
		//Controllo che l'utente abbia permesso di modifica
		SecurityChecks.checkOwnershipOrAdmin(request, map);
		boolean showDetailed;
		MapEditContext context;
		
		//Preparazione context con gli attributi che serviranno e della strategy in base al tipo di mappa
		if(map instanceof DetailedMap) {
			context = new MapEditContext(map, model, tagRepository);
			context.setStrategy(new DetailedMapEditStrategyImpl());
			showDetailed=true;
		}
		else {
			context = new MapEditContext(map, model);
			context.setStrategy(new MapEditStrategyImpl());
			showDetailed=false;
		}
		
		context.showMapToEdit();
		return showDetailed;

	}

	//Metodo che modifica la mappa sul database e ritorna la mappa modificata
	//Viene impiegato il pattern strategy
	@Override
	public Map editMap(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, MapNotFoundException, EmptyFieldException, InvalidCoordinatesException, TitleTakenException {

		//Controllo esistenza mappa e permessi utente
		Map map = findById(id);
		SecurityChecks.checkOwnershipOrAdmin(request, map);
		
		MapEditContext context;
		
		//Preparazione context e strategy in base al tipo di mappa
		if(map instanceof DetailedMap) {

        	String mapTags = request.getParameter("tags");
        	List<MapTag> updatedTags = parseTags(mapTags);
        	context = new MapEditContext(id, mapRepository, tagRepository, updatedTags, request);
        	context.setStrategy(new DetailedMapEditStrategyImpl());
      
		}
		
		else {//Se la mappa non è dettagliata
			
			//Preparazione context per trasformare la mappa in una mappa dettagliata
			if(request.getParameter("all-data") != null){
				String mapTags = request.getParameter("tags");
	        	List<MapTag> updatedTags = parseTags(mapTags);
	        	context = new MapEditContext(id, mapRepository, tagRepository, updatedTags, request);
				context.setTransform(true);
			}
			else //Preparazione context per modificare la mappa ma lasciandola non dettagliata
				
				context = new MapEditContext(id, mapRepository, request);
			
			context.setStrategy(new MapEditStrategyImpl());
			
		}
		
		return context.editMap();
	
	}
	
	//metodo da eliminare
	public Map findMapAndCheckEditPermissions(Integer id, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, MapNotFoundException{
		
		Optional<Map> mapOptional = mapRepository.findById(id);
		
		if(mapOptional.isPresent()) {
			Map map = mapOptional.get();
			if(request.getSession(false)==null || request.getSession(false).getAttribute("user")==null )
				throw new UserNotLoggedException("User is not logged in");
			else if(((User)request.getSession(false).getAttribute("user")).getId()!=map.getUser().getId() && !((User)request.getSession(false).getAttribute("user")).isAdmin())
				throw new UserUnauthorizedException("You are not authorized to access this page");	
			
			return map;
			
		}
		else
			throw new MapNotFoundException("You are trying to access a page that does not exist");
	}

	//Metodo che cancella una dal database e il rispettivo file in memoria dopo aver controllato i permessi
	@Override
	public void deleteMap(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, MapNotFoundException, StorageException {
		
		//Controllo esistenza mappa e permessi utente
		Map map = findById(id);
		SecurityChecks.checkOwnershipOrAdmin(request, map);
		
		//Eliminazione file dalla memoria
		String fileName = map.getUrl();
		try {
			Files.deleteIfExists(this.rootLocation.resolve(fileName));
		} catch (Exception e) {
			throw new StorageException("Failed to store file " + fileName);
		}

		mapRepository.delete(map);
		
	}

	//Metodo che ritorna le mappe dettagliate che hanno la query cercata dall'utente nel titolo o nella descrizione
	@Override
	public List<DetailedMap> findMapsByQuery(String query) {
		
		//Utilizzo set per evitare duplicazioni se la stessa mappa risponde sia alla ricerca per titolo che alla ricerca per descrizione
		Set<DetailedMap> mapsSet = new HashSet();
		
		//Se la mappa ha un titolo è una mappa dettagliata
		List<DetailedMap> mapsByTitle = mapRepository.findByTitleContainingIgnoreCase (query);
		mapsSet.addAll(mapsByTitle);
		
		//Se ha descrizione può essere anche mappa non dettagliata quindi è necessario un controllo
		List<Map> mapsByDescription = mapRepository.findByDescriptionContainingIgnoreCase(query);
		for (Map map : mapsByDescription)
			if (map instanceof DetailedMap)
				mapsSet.add((DetailedMap) map);
		
		List<DetailedMap> maps = new ArrayList<>(mapsSet);
		return maps;
	}

	//Metodo che ritorna le mappe localizzate tra i limiti di coordinate passati
	@Override
	public List<DetailedMap> findMapsByCoordinates(double south, double north, double west, double east) {

		return mapRepository.findByLatitudeBetweenAndLongitudeBetween(south, north, west, east);
		
	}
	
	//Metodo che crea una nuova mappa non dettagliata (I controlli sui dati sono nel controller)
	public Map createNewMap(String description, MultipartFile file, User user) throws StorageException {
		
		//Creazione nome unico per il file e salvataggio in memoria
		String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		store(file, uniqueFileName);
		
		//Creazione mappa e salvataggio nel database
		Map map = new Map(uniqueFileName, description, user);
		Map createdMap = mapRepository.save(map);
		return createdMap;
	}
	
	//Metodo che controlla l'esistenza della mappa sul database e, se esiste, la ritorna
	public Map findById(Integer id) throws MapNotFoundException {
		
		Optional<Map> mapOptional = mapRepository.findById(id);
		if(mapOptional.isPresent())
			return mapOptional.get();
		else
			throw new MapNotFoundException("Map does not exist");
	}
	
	//Metodo che ritorna tutte le mappe che non sono dettagliate
	public List<Map> getAllGuessMaps(){
		
		return mapRepository.findAllGuessMaps();
	}

}
