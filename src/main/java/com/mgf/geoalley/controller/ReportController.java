package com.mgf.geoalley.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.service.ReportService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReportController {

	private final ReportService reportService;
	
	@Autowired
	public ReportController(ReportService reportService) {
	this.reportService = reportService;
	}
	
	//Metodo che mostra la pagina per creare un report
	@GetMapping("/report/{id}")
	public String showReportPage(@PathVariable("id") Integer id, Model model, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {		
			reportService.showReportPage(id, model, request);
			return "createReport";
			
		} catch (MapNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to report a map that doesn't exist");
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}
	}
	
	//Metodo che crea il report nel database (I controlli sono nel service)
	@PostMapping("/report/{id}")
	public String createReport(@PathVariable("id") Integer id, Model model, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {
			reportService.createReport(id, model, request);
			redirectAttributes.addFlashAttribute("message", "Report sent successfully");
			return "redirect:/map/"+id;
			
		} catch (MapNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", "You are trying to report a map that doesn't exist");
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}catch (EmptyFieldException e) {
			model.addAttribute("message", e.getMessage());
			return "report/"+id;
		}		
	}
	
	//Metodo che permette a un admin di chiudere un report (Con controllo che l'utente sia un admin)
	@GetMapping("/closeReport/{id}")
	public String closeReport(@PathVariable("id") Integer id, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		
		try {
			
			SecurityChecks.checkAdmin(request);
			reportService.closeReport(id);
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
