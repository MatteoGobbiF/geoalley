package com.mgf.geoalley.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgf.geoalley.exceptions.CommentNotFoundException;
import com.mgf.geoalley.model.Comment;
import com.mgf.geoalley.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	
	@Autowired
	public CommentServiceImpl(CommentRepository commentRepository) {
		this.commentRepository=commentRepository;
	}
	
	//Aggiunge un commento al database
	@Override
	public void addComment(Comment comment) {
		
		commentRepository.save(comment);
	}
	
	//Rimuove un commento dal database (se esiste)
	public void removeById(Integer id) throws CommentNotFoundException {
		
		
		Optional<Comment> comment = commentRepository.findById(id);
		if(comment.isPresent())
			commentRepository.delete(comment.get());
		else
			throw new CommentNotFoundException("This comment does not exist");
		
	}
	
	//Ritorna un commento in base all'id
	public Comment findById(Integer id) throws CommentNotFoundException { 
		
		Optional<Comment> comment = commentRepository.findById(id);
		if(comment.isPresent())
			return comment.get();
		else
			throw new CommentNotFoundException("This comment does not exist");
		
	}

}
