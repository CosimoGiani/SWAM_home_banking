package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.UserDao;
import otpStateful.OneTimePasswordAuthenticator;
import utils.PasswordEncrypter;

@Model
public class AuthenticationController {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private OneTimePasswordAuthenticator oTPAuthenticator;

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
	
}
