package com.mgf.geoalley.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>{
	
	List<Report> findByOpenTrue(); 
}
