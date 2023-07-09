package com.mgf.geoalley.service.mapStrategy;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import com.mgf.geoalley.repository.TagRepository;
import com.mgf.geoalley.utils.SecurityChecks;

//Implementazione strategy per visualizzare una mappa dettagliata
public class DetailedMapDisplayStrategyImpl implements MapDisplayStrategy {

	private final TagRepository tagRepository;
	
	@Autowired
	public DetailedMapDisplayStrategyImpl(TagRepository tagRepository) {
		this.tagRepository=tagRepository;
	}
	
	@Override
	public void display(Map map, Model model, HttpServletRequest request) {
				
		model.addAttribute("map", map);
		
		//Ricerca dei tag della mappa sul database
		Optional<List<MapTag>> tags = tagRepository.findByMap(map);
		if (tags.isPresent()) 
			  model.addAttribute("tags", tags.get());
		
		//Se l'utente loggato è l'owner della mappa o un admin setta "auth" nel model
		if(SecurityChecks.checkOwnershipOrAdminBool(request, map))
				model.addAttribute("auth", true);
		
	}
}
