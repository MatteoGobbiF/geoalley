package com.mgf.geoalley.exceptions;

public class TitleTakenException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public TitleTakenException(String message) {
		super(message);
	}
}

