package com.mgf.geoalley.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.InvalidFileFormatException;
import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.StorageException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.PhotoService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PhotoController {
	
	private final PhotoService photoService;
	
	public PhotoController(PhotoService photoService) {
		this.photoService=photoService;
	}
	
	//Metodo che mostra la pagina con l'elenco delle foto se l'utente è loggato
	@GetMapping("/photos")
	public String photosPage(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		
		try {
			 SecurityChecks.checkLogin(request);
			 List<Photo> photos = photoService.getAllPhotos();
			 model.addAttribute("photos", photos);
			 return "photosPage";
			 
		 }catch (UserNotLoggedException e) {
				redirectAttributes.addFlashAttribute("message", "You must be logged in to see this page");
				return "redirect:/login";			 
		 }		
	}
	
	//Metodo che crea la pagina per creare una nuova foto se l'utente è loggato
	@GetMapping("/createPhoto")
	public String createPhotoPage(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
		
		try {
			 SecurityChecks.checkLogin(request);
			 return "createPhoto";
			 
		 }catch (UserNotLoggedException e) {
				redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
				return "redirect:/login";			 
		 }		
	}
	
	//Metodo che crea la foto sul database
	@PostMapping("/createPhoto")
	 public String createPhoto(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
					
		 try {
			 User user = SecurityChecks.checkLoginAndReturnUser(request);
			
			 //Parsing dei dati dalla richiesta
			 String description = request.getParameter("description");
			 MultipartFile file = request.getFile("file");
			 String contentType = file.getContentType();
	        
			 //Controlli sulla validità dei dati
			 if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("application/pdf"))
				 throw new InvalidFileFormatException("Invalid file format");
	        
			 //Creazione foto nel database e reindirizzamento alla foto creata
			 Photo createdPhoto = photoService.createNewPhoto(description, file, user);
	        
			 return "redirect:/photo/"+createdPhoto.getId();
	        			
		 }catch (UserNotLoggedException e) {
			 redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a map");
			 return "redirect:/login";
		 }catch (NumberFormatException | NullPointerException e) {
			 model.addAttribute("message", "Invalid data");
			 return "createPhoto";
		 }catch (InvalidFileFormatException | StorageException e) {
			 model.addAttribute("message", e.getMessage());
			 return "createPhoto";
		 }
	}
	
	//Metodo che ritorna la foto con {id}
	@GetMapping("/photo/{id}")
	public String showPhoto(@PathVariable Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
				
		try {
			SecurityChecks.checkLogin(request);
			Photo photo = photoService.getPhotoById(id);
			model.addAttribute("photo", photo);
			return "photo";
			
		}catch(PhotoNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to access a photo that does not exist");
			return "redirect:/";
		}catch(UserNotLoggedException e) {	
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
			return "redirect:/login";	
		}		
	}
	
	//Metodo che mostra la pagina per editare le informazioni di una foto
	@GetMapping("/photo/edit/{id}") 
	public String editPhotoShow(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {		
			photoService.showEditPhoto(id, model, request);
			return "editPhoto";
			
		} catch (PhotoNotFoundException | UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch(UserNotLoggedException e) {	
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
			return "redirect:/login";	
		}	
	}
	
	//Metodo che edita la foto
	@RequestMapping(value= "/photo/edit/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editPhoto(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {		
				Photo updatedPhoto = photoService.editPhoto(id, request);	
				//Dopo la modifica genera la pagina per visualizzare la foto appena modificata
				return "redirect:/photo/"+updatedPhoto.getId();
			
		}catch(PhotoNotFoundException | UserUnauthorizedException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
			return "redirect:/login";	
		}
	}
	
	//Metodo che cancella una mappa. I controlli sui permessi sono effetuati dal service
	@RequestMapping(value= "/photo/delete/{id}", method = {RequestMethod.POST, RequestMethod.DELETE})
	public String deletePhoto(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			photoService.deletePhoto(id, request);
			
		}catch(PhotoNotFoundException | UserUnauthorizedException | StorageException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
			return "redirect:/login";
		}
		
		//Se la cancellazione ha successo reindirizza alla home
		redirectAttributes.addFlashAttribute("message", "Photo deleted successfully");
		return "redirect:/";
	}
	
	//Metodo che mostra la pagina con l'elenco dei tentativi
	@GetMapping("/photo/{id}/guesses")
	public String showGuesses(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			SecurityChecks.checkLogin(request);
			Photo photo = photoService.getPhotoById(id);
			model.addAttribute("photo", photo);
			return "allGuesses";
						
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to upload a photo");
			return "redirect:/login";	
		} catch(PhotoNotFoundException e){
			model.addAttribute("message", e.getMessage());
			return "home";
		}		
	}
		
}
