package com.mgf.geoalley.exceptions;

public class EmailTakenException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public EmailTakenException(String message) {
		super(message);
	}
}
