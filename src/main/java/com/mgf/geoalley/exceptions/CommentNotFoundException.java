package com.mgf.geoalley.exceptions;

public class CommentNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;
	
	public CommentNotFoundException(String message) {
		super(message);
	}
}
