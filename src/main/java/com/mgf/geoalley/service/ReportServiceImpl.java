package com.mgf.geoalley.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.Report;
import com.mgf.geoalley.repository.MapRepository;
import com.mgf.geoalley.repository.ReportRepository;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final MapRepository mapRepository;
	
	@Autowired
	public ReportServiceImpl(ReportRepository reportRepository, MapRepository mapRepository) {
		this.reportRepository = reportRepository;
		this.mapRepository = mapRepository;
	}
	
	//Metodo che controlla il login dell'utente e l'esistenza della mappa in preparazione alla creazione della pagina di report
	@Override
	public void showReportPage(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, MapNotFoundException {
		
		SecurityChecks.checkLogin(request);
		Map map = findMapById(id);
		
		model.addAttribute("map", map);
		
	}
	
	//Metodo che controlla l'esistenza della mappa sul database e, se esiste, la ritorna
	public Map findMapById(Integer id) throws MapNotFoundException {
		
		Optional<Map> mapOptional = mapRepository.findById(id);
		if(mapOptional.isPresent())
			return mapOptional.get();
		else
			throw new MapNotFoundException("Map does not exist");
	}
	
	//Metodo che crea il report sul database 
	@Override
	public void createReport(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, MapNotFoundException, EmptyFieldException {
		
		//Parsing e cotnrollo descrizione
		String reportDescription = request.getParameter("description");	
		if(reportDescription== null || reportDescription.isBlank())
			throw new EmptyFieldException("This field can't be empty");
		
		//Controllo login e esistenza mappa
		SecurityChecks.checkLogin(request);
		Map map = findMapById(id);
		
		//Creazione e salvataggio report
		Report report = new Report(reportDescription, map);
		reportRepository.save(report);	
	}
	
	//Metodo che ritorna tutti i report aperti
	@Override
	public List<Report> getOpenReports(){
		return reportRepository.findByOpenTrue();
	}
	
	//Metodo che ritorna tutti i report, aperti e chiusi
	@Override
	public List<Report> getAllReports(){
		return reportRepository.findAll();
	}
	
	//Metodo che chiude un report (Il controllo che l'utente sia admin è nel controller)
	public void closeReport(Integer id) throws ReportNotFoundException {
		
		Optional<Report> reportOptional = reportRepository.findById(id);
		if(reportOptional.isPresent()) {
			Report report = reportOptional.get();
			report.close();
			reportRepository.save(report);			
		}
		else
			throw new ReportNotFoundException("You are trying to close a report that doesn't exist");

	}
	
	
}
