package com.mgf.geoalley.exceptions;

public class UserUnauthorizedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UserUnauthorizedException(String message) {
		super(message);
	}
	
}
