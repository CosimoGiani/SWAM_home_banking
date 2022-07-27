package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.ConsultantDao;
import dao.UserDao;
import model.User;
import otpStateful.OneTimePasswordAuthenticator;
// import otp.OneTimePasswordAuthenticator;
import utils.PasswordEncrypter;

@Model
public class AuthenticationController {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private OneTimePasswordAuthenticator oTPAuthenticator;
	
	@Inject
	private ConsultantDao consultantDao;
	
	public Long login(User loggingUser) { // OLD login
		User loggedUser = userDao.login(loggingUser);
		if( loggedUser == null ) {
			return null;
		} 
		return loggedUser.getId();
	}

	public boolean isEmailInDB(String email) {
		return userDao.isEmailInDB(email);
	}
	
	public boolean checkCredentialsInDB(String username, String password) {
		return userDao.checkCredentials(username, PasswordEncrypter.encrypt(password));
	}
	
	public void generateOTP(String email, String password) {
		//oTPAuthenticator.addUser(email, PasswordEncrypter.encrypt(password));
		oTPAuthenticator.generateOTP(email);
	}
	
	public void removeOTP(String email) {
		System.out.println(email);
		//oTPAuthenticator.removeUser(email);
		oTPAuthenticator.removeOTP(email);
	}
	
	public boolean checkCredentialsConsultantInDB(String identificationNumber, String password) {
		return consultantDao.checkCredentials(identificationNumber, PasswordEncrypter.encrypt(password));
	}
	
}
