package com.mgf.geoalley.service;

import com.mgf.geoalley.model.User;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mgf.geoalley.exceptions.*;

@Service
public interface UserService {

	public User createUser(User user) throws UsernameTakenException, EmailTakenException;
	
	public Optional<User> checkCredentials(String username, String password);
	
	public void showProfile(Integer id, Model model, HttpServletRequest request) throws UserNotLoggedException, UserUnauthorizedException, UserNotFoundException;

	public void showEditProfile(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException;

	public User editProfile(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException, EmptyFieldException;

	public User editPassword(Integer id, Model model, HttpServletRequest request) throws UserNotFoundException, UserUnauthorizedException, UserNotLoggedException, EmptyFieldException, PasswordsNotMatchingException, WrongPasswordException;

	public List<User> getAllUsers();
	
}

