package com.mgf.geoalley.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.MapNotFoundException;
import com.mgf.geoalley.exceptions.ReportNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.model.Report;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface ReportService {

	void showReportPage(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, MapNotFoundException;

	void createReport(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, MapNotFoundException, EmptyFieldException;

	List<Report> getOpenReports();

	void closeReport(Integer id) throws ReportNotFoundException;

	List<Report> getAllReports();

}
