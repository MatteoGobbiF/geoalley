package com.mgf.geoalley.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.InvalidCoordinatesException;
import com.mgf.geoalley.exceptions.MapNotFoundException;
import com.mgf.geoalley.exceptions.StorageException;
import com.mgf.geoalley.exceptions.TitleTakenException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;
import com.mgf.geoalley.model.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface MapService {

	public List<MapTag> parseTags(String tags);
	
	public void store(MultipartFile file, String fileName) throws StorageException;

	public DetailedMap createNewDetailedMap(String title, String mapTags, String description, int year, boolean exactYear, double latitude, double longitude, MultipartFile file, User user) throws StorageException, TitleTakenException;

	public boolean showMap(Integer id, Model model, HttpServletRequest request) throws MapNotFoundException, UserNotLoggedException;

	public boolean showEditMap(Map map, Model model, HttpServletRequest request) throws MapNotFoundException, UserNotLoggedException, UserUnauthorizedException;

	public Map editMap(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, MapNotFoundException, EmptyFieldException, InvalidCoordinatesException, TitleTakenException;
	
	public void deleteMap(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, MapNotFoundException, StorageException;

	public List<DetailedMap> findMapsByQuery(String query);

	public List<DetailedMap> findMapsByCoordinates(double south, double north, double west, double east);

	public Map createNewMap(String description, MultipartFile file, User user) throws StorageException;
	
	public Map findById(Integer id) throws MapNotFoundException;

	public List<Map> getAllGuessMaps();

}
