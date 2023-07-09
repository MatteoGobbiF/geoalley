package com.mgf.geoalley.utils;

import com.mgf.geoalley.exceptions.UserNotLoggedException;
import com.mgf.geoalley.exceptions.UserUnauthorizedException;
import com.mgf.geoalley.model.HasUser;
import com.mgf.geoalley.model.User;

import jakarta.servlet.http.HttpServletRequest;

//Classe con alcuni metodi statici usati da altre classi per verificare permessi e autenticazione
public class SecurityChecks {

	public static void checkAdmin(HttpServletRequest request) throws UserUnauthorizedException, UserNotLoggedException {
		
		if(request.getSession(false)==null || request.getSession(false).getAttribute("user")==null )
			throw new UserNotLoggedException("User is not logged in");
		if(!((User)request.getSession(false).getAttribute("user")).isAdmin())
			throw new UserUnauthorizedException("You are not authorized");	
		
	}
	
	public static User checkLoginAndReturnUser(HttpServletRequest request) throws UserNotLoggedException{
		if(request.getSession(false)==null || request.getSession().getAttribute("user")==null)
			throw new UserNotLoggedException("User is not logged in");
		else
			return (User) request.getSession().getAttribute("user");
	}
	
	public static void checkLogin(HttpServletRequest request) throws UserNotLoggedException {
		if(request.getSession(false)==null || request.getSession(false).getAttribute("user")==null )
			throw new UserNotLoggedException("User is not logged in");
	}
	
	//I prossimi tre metodi sfruttano i tipi generici e servono a definire se l'utente è il proprietario di un oggetto (sia esso mappa, foto, commento...)
	
	public static <T> boolean checkOwnership(User user, T object) {
		
		if(object instanceof HasUser) {
			HasUser obj =(HasUser) object;
			return obj.getUser().getId().equals(user.getId());
		}
		else 
			return false;
	}
	
	public static <T> void checkOwnershipOrAdmin(HttpServletRequest request, T object) throws UserNotLoggedException, UserUnauthorizedException {
		
		User user = checkLoginAndReturnUser(request);
		if(!checkOwnership(user, object))
			checkAdmin(request);		
	}

	public static <T> boolean checkOwnershipOrAdminBool(HttpServletRequest request,  T object) {
		
		if(object instanceof HasUser)
			if(request.getSession(false)!=null)
				if(((User)request.getSession(false).getAttribute("user")).getId()== ((HasUser) object).getUser().getId() || ((User)request.getSession(false).getAttribute("user")).isAdmin())
					return true;
		return false;
	}
}
