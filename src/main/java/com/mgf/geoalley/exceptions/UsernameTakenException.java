package com.mgf.geoalley.exceptions;

public class UsernameTakenException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public UsernameTakenException(String message) {
		super(message);
	}
}
