package com.mgf.geoalley.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.GuessNotFoundException;
import com.mgf.geoalley.exceptions.InvalidCoordinatesException;
import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.PhotoGuess;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.PhotoGuessService;
import com.mgf.geoalley.service.PhotoService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PhotoGuessController {

	private final PhotoGuessService photoGuessService;
	private final PhotoService photoService;
	
	@Autowired
	public PhotoGuessController(PhotoGuessService photoGuessService,  PhotoService photoService) {
		this.photoGuessService=photoGuessService;
		this.photoService=photoService;
	}
	
	//Metodo che crea un nuovo guess
	@PostMapping("/photo/{id}/guess")
	public String addGuess (@PathVariable Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			User user = SecurityChecks.checkLoginAndReturnUser(request);
			Photo photo = photoService.findById(id);
			
			//Parsing parametri
			String comment = request.getParameter("comment");
			double latitude = Double.parseDouble(request.getParameter("latitude"));
	        double longitude = Double.parseDouble(request.getParameter("longitude"));
	        
	        //Controllo validità coordinate
	        if(latitude>180 || latitude<-180 || longitude > 180 || longitude<-180)
	        	throw new InvalidCoordinatesException("Invalid coordinates");
	        
	        //Creazione e salvataggio sul database
			PhotoGuess guess = new PhotoGuess (comment, photo, user, latitude, longitude);
			photoGuessService.addGuess(guess);
			
			//Reindirizzamento alla pagina della foto
			return "redirect:/photo/"+id;
			
		}catch(UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/login";
		}catch(PhotoNotFoundException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch(InvalidCoordinatesException e) {
			redirectAttributes.addFlashAttribute("commentMessage", e.getMessage());
			return "redirect:/map/"+id;
		}		
	}
	
	//Metodo che elimina un guess (Il controllo dei permessi è nel service)
	@RequestMapping(value = "/photo/{mapId}/guess/{guessId}/delete", method = { RequestMethod.GET, RequestMethod.DELETE })
	public String deleteGuess(@PathVariable Integer photoId, @PathVariable Integer guessId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	
		try {
			SecurityChecks.checkLogin(request);
			Photo photo = photoService.findById(photoId);
			photoGuessService.removeById(guessId, request, photo);
			
			//Se ha successo reindirizza alla foto
			return "redirect:/photo/"+photoId;
		
		}catch(UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/login";
		}catch(PhotoNotFoundException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch(GuessNotFoundException e) {
			redirectAttributes.addFlashAttribute("commentMessage", e.getMessage());
			return "redirect:/photo/"+photoId;
		}
				
	}
		
}
