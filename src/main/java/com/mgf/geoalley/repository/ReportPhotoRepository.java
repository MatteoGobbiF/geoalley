package com.mgf.geoalley.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.ReportPhoto;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Integer>{

	List<ReportPhoto> findByOpenTrue(); 

}
