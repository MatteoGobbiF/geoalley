package com.mgf.geoalley.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;

@Repository
public interface MapRepository extends JpaRepository<Map, Integer>{
	
	Optional<DetailedMap> findByTitle(String title);
	
	List<DetailedMap> findByTitleContainingIgnoreCase(String keywords);
	
	List<Map> findByDescriptionContainingIgnoreCase(String keywords);
	
	List<DetailedMap> findByLatitudeBetweenAndLongitudeBetween(double south, double north, double west, double east);
	
	//Ritorna tutte le mabbe che non sono dettagliate
	@Query("SELECT m FROM Map m WHERE m.id NOT IN (SELECT dm.id FROM DetailedMap dm)")
	List<Map> findAllGuessMaps();
	
}
