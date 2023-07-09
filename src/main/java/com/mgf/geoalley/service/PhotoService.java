package com.mgf.geoalley.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.StorageException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface PhotoService {

	List<Photo> getAllPhotos();

	Photo createNewPhoto(String description, MultipartFile file, User user) throws StorageException;

	Photo getPhotoById(Integer id) throws PhotoNotFoundException ;

	void showEditPhoto(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, PhotoNotFoundException;

	Photo editPhoto(Integer id, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, PhotoNotFoundException;

	void deletePhoto(Integer id, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, StorageException, PhotoNotFoundException;

	Photo findById(Integer id) throws PhotoNotFoundException;

}
