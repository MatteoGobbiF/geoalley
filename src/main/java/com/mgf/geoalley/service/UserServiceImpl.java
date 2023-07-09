package com.mgf.geoalley.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.model.DetailedMap;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.UserRepository;
import com.mgf.geoalley.utils.SecurityChecks;

import jakarta.servlet.http.HttpServletRequest;

import com.mgf.geoalley.exceptions.*;

@Service
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	//Metodo che crea un nuovo utente nel database controllando se esiste già un utente con quell'username o quella mail
	@Override
	public User createUser(User user) throws UsernameTakenException, EmailTakenException {
		
		if(userRepository.findByUsername(user.getUsername()).isPresent())
			throw new UsernameTakenException("Username taken");
		if(userRepository.findByEmail(user.getEmail()).isPresent())
			throw new EmailTakenException("Email already in use");
		return userRepository.save(user);		
	}

	//Metodo che controlla le credenziali per il login
	@Override
	public Optional<User> checkCredentials(String username, String password) {
		
		Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
		return user;		
	}

	//Metodo che prepara gli attributi da mostrare nella schermata del profilo dopo aver controllato il permesso di vedere tale pagina
	@Override
	public void showProfile(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, UserNotFoundException {
		
		User user = findUserAndCheckPermissions(id, request);

		List<Map> detailedMaps = user.getMaps().stream().filter(map -> map instanceof DetailedMap).collect(Collectors.toList());
		List<Map> guessMaps = user.getMaps().stream().filter(map -> !(map instanceof DetailedMap)).collect(Collectors.toList());
		Set<Photo> photos = user.getPhotos();
		
		model.addAttribute("user", user);
		model.addAttribute("detailedMaps", detailedMaps);
		model.addAttribute("guessMaps", guessMaps);
		model.addAttribute("photos", photos);
	}

	//Metodo che controlla l'esistenza dell'utente e i permessi e mostra la pagina per modificare le  informazioni
	@Override
	public void showEditProfile(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException {
		
		User user = findUserAndCheckPermissions(id, request);
		model.addAttribute("user", user);

	}
	
	//Metodo che controlla l'esistenza dell'utente e i permessi di visualizzazione e modifica
	public User findUserAndCheckPermissions(Integer id, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException {
		
		Optional<User> userOptional = userRepository.findById(id);
		if(userOptional.isPresent()) {
			User user = userOptional.get();		
			
			//Controllo login
			User loggedUser = SecurityChecks.checkLoginAndReturnUser(request);
			
			//Controllo se il profilo a cui si tenta di accedere e dell'utente loggato/ se l'utente loggato è un admin
			if(loggedUser.getId()!=user.getId() && !((User)request.getSession(false).getAttribute("user")).isAdmin())
				throw new UserUnauthorizedException("You are not authorized to access this page");				
			return user;
		} else
			throw new UserNotFoundException("User does not exist");
	}
	
	//Metodo che edita le informazioni del profilo (username e password)
	public User editProfile(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException, EmptyFieldException {
		
		User user = findUserAndCheckPermissions(id, request);
		
		//Parsing e controllo validità dati
		String username = request.getParameter("username");
        String email = request.getParameter("email");
        if (username == null || email == null || username.isBlank() || email.isBlank())
			throw new EmptyFieldException("Field can't be empty");
        
        //Modifica dati dell'utente e salvataggio nel database
        user.setUsername(username);
        user.setEmail(email);        
        return userRepository.save(user);
 		
	}
	
	//Metodo che edita la password dell'utente dopo alcuni controlli
	public User editPassword(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException, EmptyFieldException, PasswordsNotMatchingException, WrongPasswordException {
		
		User user = findUserAndCheckPermissions(id, request);
		
		//Parsing e controllo validità dati
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		String repeatPassword = request.getParameter("repeatPassword");
		if(oldPassword==null || newPassword==null || repeatPassword==null || oldPassword.isBlank() || newPassword.isBlank())
			throw new EmptyFieldException("Field can't be empty");
		if(!newPassword.equals(repeatPassword))
			throw new PasswordsNotMatchingException("The two passwords don't match");
		if(!user.getPassword().equals(oldPassword))
			throw new WrongPasswordException("Wrong Password");
		
		//Modifica password e salvataggio nel database
		user.setPassword(newPassword);
		return userRepository.save(user);
		
	}
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	
	
}
