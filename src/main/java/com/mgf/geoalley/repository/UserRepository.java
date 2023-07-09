package com.mgf.geoalley.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgf.geoalley.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	public Optional<User> findByUsername(String username);

	public Optional<User> findByEmail(String email);

	public Optional<User> findByUsernameAndPassword(String username, String password);
}
