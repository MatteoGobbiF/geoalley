package com.mgf.geoalley.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.Comment;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.CommentService;
import com.mgf.geoalley.service.MapService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CommentController {
	
	private final CommentService commentService;
	private final MapService mapService;
	
	@Autowired
	public CommentController(CommentService commentService, MapService mapService) {
		this.commentService=commentService;
		this.mapService=mapService;
	}
	
	//Metodo per aggiungere un nuovo commento alla mappa {id}
	@PostMapping("/map/{id}/comment")
	public String addComment (@PathVariable Integer id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			
			//I metodi che ritornano user e map controllano il login e l'esistenza della mappa
			User user = SecurityChecks.checkLoginAndReturnUser(request);
			Map map = mapService.findById(id);
			
			String content = request.getParameter("content");
			if(content==null || content.isBlank())
				throw new EmptyFieldException("You can't add an empty comment");
			
			Comment comment = new Comment(user, map, content);
			commentService.addComment(comment);
			
			return "redirect:/map/"+id;
		
		//Reindirizzamenti manca qualcosa
		}catch(UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to do that");
			return "redirect:/login";
		}catch(MapNotFoundException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch(EmptyFieldException e) {
			redirectAttributes.addFlashAttribute("commentMessage", e.getMessage());
			return "redirect:/map/"+id;
		}		
	}
	
	//Metodo per cancellare dalla mappa {mapId} il commento {commentId}
	@RequestMapping(value = "/map/{mapId}/comment/{commentId}/delete", method = { RequestMethod.GET, RequestMethod.DELETE })
	public String deleteComment(@PathVariable Integer mapId, @PathVariable Integer commentId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	
		try {
			
			//Controlli di login, di esistenza della mappa e di esistenza del commento nei rispettivi metodi
			User user = SecurityChecks.checkLoginAndReturnUser(request);
			Map map = mapService.findById(mapId);
			Comment comment = commentService.findById(commentId);
			
			//Controllo permesso dell'utente loggato
			if(!SecurityChecks.checkOwnership(user, map))
				SecurityChecks.checkOwnershipOrAdmin(request, comment);
			
			commentService.removeById(commentId);
			return "redirect:/map/"+mapId;
		
		}catch(UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You must be logged in to do that");
			return "redirect:/login";
		}catch(MapNotFoundException | UserUnauthorizedException e){
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch(CommentNotFoundException e) {
			redirectAttributes.addFlashAttribute("commentMessage", e.getMessage());
			return "redirect:/map/"+mapId;
		}
		
		
	}
	
}