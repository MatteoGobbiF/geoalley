package com.mgf.geoalley.service.mapStrategy;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.model.Map;

@Service
public interface MapDisplayStrategy {
	public void display(Map map, Model model, HttpServletRequest request) throws UserNotLoggedException;
}
