package com.mgf.geoalley.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.MapTag;

@Repository
public interface TagRepository extends JpaRepository<MapTag, Integer>{

	Optional<List<MapTag>> findByMap(Map map);

}
