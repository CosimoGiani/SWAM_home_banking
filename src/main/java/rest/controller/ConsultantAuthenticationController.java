package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import ConsultantAuthentication.ConsultantAuthenticator;
import dao.ConsultantDao;
import utils.PasswordEncrypter;

@Model
public class ConsultantAuthenticationController {
	
	@Inject
	private ConsultantDao consultantDao;
	
	@Inject 
	private ConsultantAuthenticator consultantAuthenticator; 
	
	public boolean checkConsultantCredentials(String identificationNumber, String password) {
		return consultantDao.checkCredentials(identificationNumber, PasswordEncrypter.encrypt(password));
	}
	
	public String generateToken(String identificationNumber) {
		return consultantAuthenticator.generateToken(identificationNumber);
	}
	
	public void removeToken(String identificationNumber) {
		consultantAuthenticator.removeToken(identificationNumber);
	}
}
