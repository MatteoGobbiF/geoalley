package com.mgf.geoalley.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.ReportNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.service.ReportPhotoService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReportPhotoController {

	private final ReportPhotoService reportPhotoService;
	
	@Autowired
	public ReportPhotoController(ReportPhotoService reportPhotoService) {
	this.reportPhotoService = reportPhotoService;
	}
	
	//Metodo che mostra la pagina per reportare una foto
	@GetMapping("/photo/report/{id}")
	public String showReportPage(@PathVariable("id") Integer id, Model model, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {		
			reportPhotoService.showReportPage(id, model, request);
			return "createReportPhoto";
			
		} catch (PhotoNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to report a photo that doesn't exist");
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}
	}
	
	//Metodo che crea un report (i controlli sono nel service)
	@PostMapping("/photo/report/{id}")
	public String createReport(@PathVariable("id") Integer id, Model model, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {
			reportPhotoService.createReport(id, model, request);
			redirectAttributes.addFlashAttribute("message", "Report sent successfully");
			return "redirect:/photo/"+id;
			
		} catch (PhotoNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to report a photo that doesn't exist");
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}catch (EmptyFieldException e) {
			model.addAttribute("message", e.getMessage());
			return "report/"+id;
		}
		
	}
	
	//Metodo che permette di chiudere un report se l'utente è un admin
	@GetMapping("/closePhotoReport/{id}")
	public String closeReport(@PathVariable("id") Integer id, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {
			SecurityChecks.checkAdmin(request);
			reportPhotoService.closeReport(id);
			return "redirect:/admin";
		}catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to do that");
			return "redirect:/login";
		}catch (UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		}catch (ReportNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin";
		}

		
	}	
	
}

