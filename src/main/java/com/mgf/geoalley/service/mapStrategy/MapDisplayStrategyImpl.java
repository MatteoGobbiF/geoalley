package com.mgf.geoalley.service.mapStrategy;


import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

//Implementazione strategy per visualizzare una mappa non dettagliata
@Service
public class MapDisplayStrategyImpl implements MapDisplayStrategy {

	@Override
	public void display(Map map, Model model, HttpServletRequest request) throws UserNotLoggedException {
		
		User user = SecurityChecks.checkLoginAndReturnUser(request);
		
		//Se l'utente è il proprietario della mappa o un admin si setta l'attribute "auth" a true, servirà al thymeleaf template
		if(SecurityChecks.checkOwnership(user, map) || user.isAdmin())
			model.addAttribute("auth", true);	
		
		model.addAttribute("map", map);
				
	}

}
