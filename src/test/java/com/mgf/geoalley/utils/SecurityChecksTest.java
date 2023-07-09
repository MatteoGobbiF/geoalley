package com.mgf.geoalley.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.mgf.geoalley.exceptions.*;
import com.mgf.geoalley.model.Comment;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;

import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityChecksTest {

	private SecurityChecks securityChecks;
	private HttpServletRequest request;
	private User user;
	private User admin;
	private User notAdmin;
	private Map map;
	private Photo photo;
	private Comment comment;

	@BeforeEach
	public void setUp() {
		securityChecks = new SecurityChecks();
	    request = mock(HttpServletRequest.class);
	    user = mock(User.class);
	    admin = mock(User.class);
	    notAdmin = mock(User.class);
	    map = mock(Map.class);
	    photo = mock(Photo.class);
	    comment = mock(Comment.class);
	}

	@Test
	public void testCheckAdmin_UserNotLogged() {
	    when(request.getSession(false)).thenReturn(null);
	    assertThrows(UserNotLoggedException.class, () -> SecurityChecks.checkAdmin(request));
	}

	@Test
	public void testCheckAdmin_UserNotAuthorized() {
	    when(request.getSession(false)).thenReturn(mock(HttpSession.class));
	    when(request.getSession(false).getAttribute("user")).thenReturn(notAdmin);
	    when(notAdmin.isAdmin()).thenReturn(false);
	    assertThrows(UserUnauthorizedException.class, () -> SecurityChecks.checkAdmin(request));
	}

	@Test
	public void testCheckAdmin_UserAuthorized() {
	    when(request.getSession(false)).thenReturn(mock(HttpSession.class));
	    when(request.getSession(false).getAttribute("user")).thenReturn(admin);
	    when(admin.isAdmin()).thenReturn(true);
	    assertDoesNotThrow(() -> SecurityChecks.checkAdmin(request));
	}

	@Test
	public void testCheckLoginAndReturnUser_UserNotLogged() {
	    when(request.getSession(false)).thenReturn(null);
	    assertThrows(UserNotLoggedException.class, () -> SecurityChecks.checkLoginAndReturnUser(request));
	}

	@Test
	public void testCheckLogin_UserNotLogged() {
	    when(request.getSession(false)).thenReturn(null);
	    assertThrows(UserNotLoggedException.class, () -> SecurityChecks.checkLogin(request));
	}

	@Test
	public void testCheckLogin_UserLogged() {
	    when(request.getSession(false)).thenReturn(mock(HttpSession.class));
	    when(request.getSession(false).getAttribute("user")).thenReturn(user);
	    assertDoesNotThrow(() -> SecurityChecks.checkLogin(request));
	}

}

		
		
