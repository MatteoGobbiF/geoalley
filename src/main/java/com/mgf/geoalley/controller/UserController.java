package com.mgf.geoalley.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgf.geoalley.exceptions.EmailTakenException;
import com.mgf.geoalley.exceptions.EmptyFieldException;
import com.mgf.geoalley.exceptions.PasswordsNotMatchingException;
import com.mgf.geoalley.exceptions.UserNotFoundException;
import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.exceptions.UsernameTakenException;
import com.mgf.geoalley.exceptions.WrongPasswordException;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	
	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
	this.userService = userService;
	}

	//Metodo che processa il login
	@PostMapping("/login")
	public String processLogin(@RequestParam("username") String username, @RequestParam("password") String password,  Model model, HttpServletRequest request) {
		
		//Controllo credenziali
		Optional<User> user = userService.checkCredentials(username, password);
		
		//Se le credenziali coincidono con quelle di un utente, lo salva nella sessione e reindirizza alla home
		if (user.isPresent()) {
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user.get());
			return "redirect:/";
		}
		else {//Se non trova nessun utente, rimanda alla schermata di login
			model.addAttribute("message", "Bad Credentials");
			return "login";
		}
	}
	
	//Metodo che registra un nuovo utente
	@PostMapping("/register")
	public String registrateUser(@ModelAttribute User user, @RequestParam String re_password, Model model) {
		
		try {
			//Controllo validità parametri
			if (user.getEmail()==null || user.getPassword()==null || user.getUsername()==null || user.getEmail().isBlank() || user.getPassword().isBlank() || user.getUsername().isBlank())
				throw new EmptyFieldException("Field can't be empty");
			if (!user.getPassword().equals(re_password))
				throw new PasswordsNotMatchingException("The two passwords don't match");
			
			//Creazione nel database del nuovo utente
			userService.createUser(user);
		
		}catch(EmptyFieldException | PasswordsNotMatchingException | UsernameTakenException | EmailTakenException e) {
			model.addAttribute("message", e.getMessage());
			return "registration";
		}		
		model.addAttribute("message", "Registration Successfull");
		return "registration";
	}
	
	//Metodo che genera la pagina "profilo" (I controlli sono nel service)
	@GetMapping("/profile/{id}")
	public String showProfile(@PathVariable Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
			try {
				userService.showProfile(id, model, request);
				return "profile";
			} catch (UserNotFoundException | UserUnauthorizedException e) {
				redirectAttributes.addFlashAttribute("message", e.getMessage());
				return "redirect:/";
			} catch (UserNotLoggedException e) {
				redirectAttributes.addFlashAttribute("message", "You need to be logged in to do that");
				return "redirect:/login";
			}
	}

	//Metodo che genera la pagina per editare il profilo (i controlli sui permessi sono nel service)
	@GetMapping("/profile/edit/{id}")
	public String showEditProfile(@PathVariable Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			userService.showEditProfile(id, model, request);
			return "editProfile";
			
		} catch (UserNotFoundException | UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to do that");
			return "redirect:/login";
		}
	}
	
	//Metodo che permette di modificare username e email (I controlli sui permessi sono nel service)
	@RequestMapping(value= "/profile/edit/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editProfile(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		try {
			User user = userService.editProfile(id, model, request);
			return showProfile(user.getId(), model, request, redirectAttributes);
	
		} catch (UserNotFoundException | UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to do that");
			return "redirect:/login";
		} catch (EmptyFieldException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/profile/edit/"+id;
		}     
	}
	
	//Metodo che permette di modificare la password (I controlli sui permessi sono nel service)
	@RequestMapping(value= "/profile/editPassword/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
	public String editProfilePassword(@PathVariable("id") Integer id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	
		try {
			User user = userService.editPassword(id, model, request);
			if(request.getSession(false)!=null)
				request.getSession(false).invalidate();
			
			//Se ha avuto successo, invalida la sessione e reindirizza alla schermata di login
			redirectAttributes.addFlashAttribute("message", "Password changed successfully, you have been logged out");
			return "redirect:/login";
			
		} catch (UserNotFoundException | UserUnauthorizedException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/";
		} catch (UserNotLoggedException e) {
			redirectAttributes.addFlashAttribute("message", "You need to be logged in to do that");
			return "redirect:/login";
		} catch (EmptyFieldException | PasswordsNotMatchingException | WrongPasswordException e) {
			redirectAttributes.addFlashAttribute("passMessage", e.getMessage());
			return "redirect:/profile/edit/"+id;
		}	
	}
}
