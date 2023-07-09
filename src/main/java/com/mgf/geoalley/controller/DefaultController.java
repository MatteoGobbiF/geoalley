package com.mgf.geoalley.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

//Controller che gestisce funzioni base come la generazione delle schermate home, login e registration i il logout
@Controller
public class DefaultController {
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	  
	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}
		
	@GetMapping("/registration")
	public String showRegistration() {
		return "registration";
	}
		
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		if(request.getSession(false)!=null)
			request.getSession(false).invalidate();
		return "redirect:/";
	}

	
}
