package com.mgf.geoalley.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer>{

	
}
