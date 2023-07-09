package com.mgf.geoalley.service;

import org.springframework.stereotype.Service;

import com.mgf.geoalley.exceptions.CommentNotFoundException;
import com.mgf.geoalley.model.Comment;

@Service
public interface CommentService {

	void addComment(Comment comment);

	void removeById(Integer commentId) throws CommentNotFoundException;

	Comment findById(Integer commentId) throws CommentNotFoundException;

}
