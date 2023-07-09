package com.mgf.geoalley.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.PhotoGuess;
import com.mgf.geoalley.repository.PhotoGuessRepository;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

import com.mgf.geoalley.exceptions.*;

@Service
public class PhotoGuessServiceImpl implements PhotoGuessService {

	private final PhotoGuessRepository photoGuessRepository;
	
	@Autowired
	public PhotoGuessServiceImpl(PhotoGuessRepository photoGuessRepository) {
		this.photoGuessRepository=photoGuessRepository;
	}
	
	//Metodo che crea una nuova guess nel database
	@Override
	public void addGuess(PhotoGuess guess) {
		
		photoGuessRepository.save(guess);
	}
	
	//Metodo che dopo aver controllato se l'utente è proprietario della guess, della foto o admin elimina la guess
	@Override
	public void removeById(Integer id, HttpServletRequest request, Photo photo) throws GuessNotFoundException {
		
		Optional<PhotoGuess> guess = photoGuessRepository.findById(id);
		if(guess.isPresent())
			if(SecurityChecks.checkOwnershipOrAdminBool(request, guess.get()) || SecurityChecks.checkOwnershipOrAdminBool(request, photo))
			photoGuessRepository.delete(guess.get());
		else
			throw new GuessNotFoundException("This comment does not exist");
		
	}

}
