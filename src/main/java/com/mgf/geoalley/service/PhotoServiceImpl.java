package com.mgf.geoalley.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.StorageException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.PhotoRepository;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	
	@Autowired
	public PhotoServiceImpl(PhotoRepository photoRepository) {
		this.photoRepository=photoRepository;
	}
	
	private final Path rootLocation = Paths.get("src/main/resources/static");
	
	//Metodo che ritorna la lista di tutte le foto
	@Override
	public List<Photo> getAllPhotos() {
		
		return photoRepository.findAll();
	}
	
	//Metodo che salva in memoria il file
	public void store(MultipartFile file, String fileName) throws StorageException {
			
			try {
	            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
	        } catch (Exception e) {
	            throw new StorageException("Failed to store file " + file.getOriginalFilename());
	        }			
	}
	
	//Metodo che crea la nuova foto sul database dopo averla salvata il memoria
	public Photo createNewPhoto(String description, MultipartFile file, User user) throws StorageException {
			
		//Creaizione nome file unico e salvataggio in memoria
		String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		store(file, uniqueFileName);
		
		//Creazione foto e salvataggio sul database
		Photo photo = new Photo(uniqueFileName, description, user);
		return photoRepository.save(photo);
	}
	
	//Metodo che ritorna (se esiste) la foto {id}
	public Photo getPhotoById(Integer id) throws PhotoNotFoundException {
			
			Optional<Photo> photo = photoRepository.findById(id);
			
			if(photo.isPresent())
				return photo.get();
			else
				throw new PhotoNotFoundException("Photo not found with id " + id);
	}
	
	//Metodo che dopo aver controllato se la foto esiste e i permessi prepara la foto nel model
	public void showEditPhoto(Integer id, Model model, HttpServletRequest request) throws PhotoNotFoundException, UserNotLoggedException, UserUnauthorizedException {
		
		Photo photo = getPhotoById(id);
		SecurityChecks.checkOwnershipOrAdmin(request, photo);
		model.addAttribute("photo", photo);
		
	}
	
	//Metodo che dopo aver controllato se la foto esiste e i permessi la edita
	public Photo editPhoto(Integer id, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, PhotoNotFoundException{

		Photo photo = getPhotoById(id);
		SecurityChecks.checkOwnershipOrAdmin(request, photo);
		photo.setDescription(request.getParameter("description"));
		return photoRepository.save(photo);
		
	}
	
	//Metodo che elimina una foto dopo averne verificato l'esistenza e i permessi
	public void deletePhoto(Integer id, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, StorageException, PhotoNotFoundException {

		Photo photo = getPhotoById(id);
		SecurityChecks.checkOwnershipOrAdmin(request, photo);
		String fileName = photo.getUrl();
		
		//Eliminazione file dalla memoria
		try {
			Files.deleteIfExists(this.rootLocation.resolve(fileName));
		} catch (Exception e) {
			throw new StorageException("Failed to store file " + fileName);
		}
		
		//Eliminazione foto dal database
		photoRepository.delete(photo);		
	}
	
	//Metodo che ritorna la foto per id (se esiste)
	public Photo findById(Integer id) throws PhotoNotFoundException {
			
		Optional<Photo> photo = photoRepository.findById(id);
		if(photo.isPresent())
			return photo.get();
		else
			throw new PhotoNotFoundException("Photo does not exist");
	}

}
