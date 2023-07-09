package com.mgf.geoalley.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.ReportNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.model.ReportPhoto;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface ReportPhotoService {

	void showReportPage(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, PhotoNotFoundException;

	void createReport(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, PhotoNotFoundException, EmptyFieldException;

	List<ReportPhoto> getOpenReports();

	void closeReport(Integer id) throws ReportNotFoundException;

	List<ReportPhoto> getAllReports();

}
