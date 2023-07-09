package com.mgf.geoalley.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.PhotoGuess;

@Repository
public interface PhotoGuessRepository extends JpaRepository<PhotoGuess, Integer> {

}
