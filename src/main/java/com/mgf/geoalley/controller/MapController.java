package com.mgf.geoalley.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.MapService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MapController {
	
	private final MapService mapService;
	
	@Autowired
	public MapController(MapService mapService) {
		this.mapService=mapService;
	}
	
	//Metodo che genera la pagina "create map" dopo aver controllato se l'utente è loggato. Se non è loggato rimanda alla schermata di login
	@GetMapping("/createMap")
	public String createMapPage(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		try {		
			SecurityChecks.checkLogin(request);
			return "createMap";		
			
		} catch (UserNotLoggedException e) {			
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
			return "redirect:/login";
		}		
	}
	
	//Metodo che crea la mappa nel database dopo opportuni controlli sui dati ricevuti
	@PostMapping("/createMap")
	public String createMap(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
					
		try {
			User user = SecurityChecks.checkLoginAndReturnUser(request);
			
			//Parsing dei dati dalla richiesta
			String title = request.getParameter("title");
	        String tags = request.getParameter("tags");
	        String description = request.getParameter("description");
	        int year = Integer.parseInt(request.getParameter("year"));
	        String exactYearValue = request.getParameter("exactYear");
	        double latitude = Double.parseDouble(request.getParameter("latitude"));
	        double longitude = Double.parseDouble(request.getParameter("longitude"));
	        
	        MultipartFile file = request.getFile("file");
	        String contentType = file.getContentType();
	        
	        boolean exactYear = false;
	        if (exactYearValue != null)
	            exactYear = true;

	        //Controlli sulla validità dei dati
	        if(title.isBlank() || title==null)
	        	throw new EmptyFieldException("Title can't be null");
	        if(latitude>180 || latitude<-180 || longitude > 180 || longitude<-180)
	        	throw new InvalidCoordinatesException("Invalid coordinates");
	        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("application/pdf") && !contentType.equals("application/octet-stream"))
	            throw new InvalidFileFormatException("Invalid file format");
	        	        
	        //Creazione mappa nel database e reindirizzamento alla mappa creata
	        DetailedMap createdMap = mapService.createNewDetailedMap(title, tags, description, year, exactYear, latitude, longitude, file, user);
	        
	        return "redirect:/map/"+createdMap.getId();
	        
		//Opportuni reindirizzamenti in caso qualcosa sia andato storto	
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
			return "redirect:/login";
		}catch (NumberFormatException | NullPointerException e) {
			model.addAttribute("message", "Invalid data "+e.getMessage()+e.getCause());
			return "createMap";
	    }catch (EmptyFieldException | InvalidCoordinatesException | InvalidFileFormatException | StorageException | TitleTakenException e) {
	    	model.addAttribute("message", e.getMessage());
			return "createMap";
	    }
	}
	
	//Metodo che risponde alla get rimandando a una pagina con la mappa e i suoi dati
	//I model attributes opportuni sono settati dal mapService
	@GetMapping("/map/{id}")
	public String showMap(@PathVariable Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
				
		try {
			boolean showDetailed = mapService.showMap(id, model, request);
			if(showDetailed)
				return "map";
			else
				return "guessMap";
			
		}catch(MapNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to access a map that does not exist");
			return "redirect:/";
		}catch(UserNotLoggedException e) {	//Solo per le mappe della sezione "guess"		
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}		
	}
	
	//Metodo che genera la pagina per editare una mappa, ma solo se l'utente loggato è autorizzato (Se ne è il proprietario o se è un admin)
	@GetMapping("/edit/{id}") 
	public String editMapShow(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {	
			Map map = mapService.findById(id);
			boolean showDetailed = mapService.showEditMap(map, model, request);
			if(showDetailed)
				return "editDetailedMap";
			else
				return "editMap";
			
		} catch (MapNotFoundException | UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to do that");
			return "redirect:/login";
		}
	}
	
	//Metodo che risponde a put e post per modificare i dati di una mappa. I controlli della validità dei dati vengono fatti nel service
	@RequestMapping(value= "/edit/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editMap(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {		
				Map updatedMap = mapService.editMap(id,  model,  request);	
				//Dopo la modifica genera la pagina per visualizzare la mappa appena modificata
				return "redirect:/map/"+updatedMap.getId();
			
		}catch(MapNotFoundException | UserUnauthorizedException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged to do that");
			return "redirect:/login";
		} catch (EmptyFieldException | InvalidCoordinatesException | TitleTakenException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return editMapShow(id, model, request, redirectAttributes);
		}		
	}
	
	//Metodo che cancella una mappa. I controlli sui permessi sono effetuati dal service
	@RequestMapping(value= "/delete/{id}", method = {RequestMethod.POST, RequestMethod.DELETE})
	public String deleteMap(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			
			mapService.deleteMap(id, model, request);
			redirectAttributes.addFlashAttribute("message", "Map deleted successfully");
			return "redirect:/";
			
		}catch(MapNotFoundException | UserUnauthorizedException | StorageException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to do that");
			return "redirect:/login";
		}

	}
	
	//Metodo per effettuare la ricerca attraverso keyword nel titolo e nella descrizione delle mappe
	@GetMapping("/search")
	public String searchByQuery(@RequestParam("query") String query, Model model) {
		
		if(query.isEmpty())
			return "redirect:/";
		
		List<DetailedMap> maps = mapService.findMapsByQuery(query);
		model.addAttribute("maps", maps);
		
		//Aggiunge i tag a una lista da mettere nel model
		List<String> uniqueTags = new ArrayList<>();
		for (DetailedMap map : maps)
			for (MapTag tag : map.getTags())
				if(!uniqueTags.contains(tag.getTagName()))
					uniqueTags.add(tag.getTagName());
		
		model.addAttribute("tags", uniqueTags);

		return "search";
	}
	
	//Metodo per effettuare la ricerca attraverso coordinate
	@GetMapping("/searchFromMap")
	public String searchFromMap(@RequestParam double north, @RequestParam double east, @RequestParam double south, @RequestParam double west, Model model) {
		
		List<DetailedMap> maps = mapService.findMapsByCoordinates(south, north, west, east);
		model.addAttribute("maps", maps);
		
		//Aggiunge i tag a una lista da mettere nel model
		List<String> uniqueTags = new ArrayList<>();
		for (DetailedMap map : maps)
			for (MapTag tag : map.getTags())
				if(!uniqueTags.contains(tag.getTagName()))
					uniqueTags.add(tag.getTagName());
				
		model.addAttribute("tags", uniqueTags);

		return "search";		 
	 }
	
	//Metodo che genera la pagina per caricare una nuova mappa senza informazioni dopo aver controllato il login
	@GetMapping("/createGuessMap")
	public String createGuessMapPage(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		try {
			SecurityChecks.checkLogin(request);
			return "createGuessMap";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
			return "redirect:/login";			
		}
	}
	
	//Metodo che crea la nuova mappa nel database
	@PostMapping("/createGuessMap")
	public String createGuessMap(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
					
		try {
			
			//Controllo login
			User user = SecurityChecks.checkLoginAndReturnUser(request);
			
			//Parsing dei dati dalla richiesta
        	String description = request.getParameter("description");
	        MultipartFile file = request.getFile("file");
	        String contentType = file.getContentType();
	        
	        //Controlli sulla validità dei dati
	        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("application/pdf") && !contentType.equals("application/octet-stream"))
	            throw new InvalidFileFormatException("Invalid file format");
	        
	        
	        //Creazione mappa nel database e reindirizzamento alla mappa creata
	        Map createdMap = mapService.createNewMap(description, file, user);
	        
	        return "redirect:/map/"+createdMap.getId();
	        
			
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
			return "redirect:/login";
		}catch (NumberFormatException | NullPointerException e) {
			model.addAttribute("message", "Invalid data");
			return "createMap";
	    }catch (InvalidFileFormatException | StorageException e) {
	    	model.addAttribute("message", e.getMessage());
			return "createMap";
	    }
	}
	
	//Metodo che genera la pagina con l'elenco di mappe incomplete dopo aver controllato se l'utente è loggato
	@GetMapping("/guess")
	public String showGuessMaps(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		try {
			SecurityChecks.checkLogin(request);
			List<Map> maps = mapService.getAllGuessMaps();
			model.addAttribute("maps", maps);
			return "guess";
			 
	 	}catch (UserNotLoggedException e) {
	 		redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
	 		return "redirect:/login";			 
	 	}
				 
	}
	 
	 
}
