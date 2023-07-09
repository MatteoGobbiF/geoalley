package com.mgf.geoalley.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.Report;
import com.mgf.geoalley.model.ReportPhoto;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.ReportPhotoService;
import com.mgf.geoalley.service.ReportService;
import com.mgf.geoalley.service.UserService;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {
	
	private final UserService userService;
	private final ReportService reportService;
	private final ReportPhotoService reportPhotoService;
	
	@Autowired
	public AdminController(UserService userService, ReportService reportService, ReportPhotoService reportPhotoService) {
		this.reportService=reportService;
		this.userService=userService;
		this.reportPhotoService=reportPhotoService;
	}
	
	//Genera la pagina "admin" dopo aver controllato l'autorizzazione
	@GetMapping("/admin")
	public String adminPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		try {
			//Controllo se l'utente è loggato e se è admin
			SecurityChecks.checkAdmin(request);
			
			//Preparazione degli attributi da mostrare nella pagina
			List<User> users = userService.getAllUsers();
			List<Report> reports = reportService.getOpenReports();
			List<ReportPhoto> reportPhotos = reportPhotoService.getOpenReports();
			model.addAttribute("users", users);
			model.addAttribute("reports", reports);
			model.addAttribute("reportPhotos", reportPhotos);
			
			return "admin";
			
		//Reindirizzamenti se non è loggato o autorizzato
		} catch (UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}
		
	}

	//Genera la pagina "allReports" dopo aver controllato l'autorizzazione
	@GetMapping("/admin/allReports")
	public String adminAllReportsPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

		try {
			//Controllo se l'utente è loggato e se è admin
			SecurityChecks.checkAdmin(request);
			
			//Preparazione degli attributi da mostrare nella pagina			
			List<Report> reports = reportService.getAllReports();
			List<ReportPhoto> reportPhotos = reportPhotoService.getAllReports();
			model.addAttribute("reports", reports);
			model.addAttribute("reportPhotos", reportPhotos);
			
			return "adminAllReports";
		
		//Reindirizzamenti se non è loggato o autorizzato
		} catch (UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to access that page");
			return "redirect:/login";
		}
	}
		
	
}
