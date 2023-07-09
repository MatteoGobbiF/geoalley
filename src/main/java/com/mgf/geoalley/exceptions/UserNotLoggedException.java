package com.mgf.geoalley.exceptions;

public class UserNotLoggedException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public UserNotLoggedException(String message) {
		super(message);
	}
}
