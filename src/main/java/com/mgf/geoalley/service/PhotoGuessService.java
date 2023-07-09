package com.mgf.geoalley.service;

import org.springframework.stereotype.Service;

import com.mgf.geoalley.exceptions.GuessNotFoundException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.PhotoGuess;
import jakarta.servlet.http.HttpServletRequest;

@Service
public interface PhotoGuessService {

	void addGuess(PhotoGuess guess);

	void removeById(Integer guessId, HttpServletRequest request, Photo photo) throws GuessNotFoundException;

}
