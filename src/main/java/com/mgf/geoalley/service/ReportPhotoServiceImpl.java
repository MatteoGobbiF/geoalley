package com.mgf.geoalley.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.ReportNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.ReportPhoto;
import com.mgf.geoalley.repository.PhotoRepository;
import com.mgf.geoalley.repository.ReportPhotoRepository;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ReportPhotoServiceImpl implements ReportPhotoService {

	private final ReportPhotoRepository reportPhotoRepository;
	private final PhotoRepository photoRepository;
	
	@Autowired
	public ReportPhotoServiceImpl(ReportPhotoRepository reportPhotoRepository, PhotoRepository photoRepository) {
		this.reportPhotoRepository = reportPhotoRepository;
		this.photoRepository = photoRepository;
	}
	
	//Metodo che effettua i controlli e poi prepara il model per mostrare la pagina per reportare una foto
	@Override
	public void showReportPage(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, PhotoNotFoundException {
		
		SecurityChecks.checkLogin(request);
		Photo photo = findPhotoById(id);
		model.addAttribute("photo", photo);		
	}
	
	//Metodo che trova la foto per id (se esiste)
	public Photo findPhotoById(Integer id) throws PhotoNotFoundException {
		
		Optional<Photo> photo = photoRepository.findById(id);		
		if(photo.isPresent())		
			return photo.get();

		else
			throw new PhotoNotFoundException("You are trying to access a page that does not exist");		
	}
	
	//Metodo che crea il report nel database
	@Override
	public void createReport(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, PhotoNotFoundException, EmptyFieldException {
		
		//Parsing e controllo descrizione
		String reportDescription = request.getParameter("description");		
		if(reportDescription== null || reportDescription.isBlank())
			throw new EmptyFieldException("This field can't be empty");
		
		//Controllo login utente e esistenza foto
		SecurityChecks.checkLogin(request);
		Photo photo = findPhotoById(id);
		
		//Creazione e salvataggio report
		ReportPhoto report = new ReportPhoto(reportDescription, photo);
		reportPhotoRepository.save(report);	
	}
	
	//Metodo che ritorna tutti i report aperti
	@Override
	public List<ReportPhoto> getOpenReports(){
		return reportPhotoRepository.findByOpenTrue();
	}
	
	//Metodo che ritorna tutti i report (aperti e chiusi)
	public List<ReportPhoto> getAllReports(){
		return reportPhotoRepository.findAll();
	}
	
	//Metodo che permette di chiudere un report (il controllo che l'utente sia admin è nel controller)
	public void closeReport(Integer id) throws ReportNotFoundException {
		
		Optional<ReportPhoto> reportOptional = reportPhotoRepository.findById(id);
		if(reportOptional.isPresent()) {
			ReportPhoto report = reportOptional.get();
			report.close();
			reportPhotoRepository.save(report);			
		}
		else
			throw new ReportNotFoundException("You are trying to close a report that doesn't exist");
	}	
}

