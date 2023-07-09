package com.mgf.geoalley.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mgf.geoalley.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	
}
