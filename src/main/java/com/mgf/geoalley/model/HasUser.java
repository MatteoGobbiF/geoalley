package com.mgf.geoalley.model;

//Interfaccia implementata da tutti i model che hanno come attributo un utente, serve per i metodi checkOwnership di securityChecks
public interface HasUser {

	User getUser();
}
