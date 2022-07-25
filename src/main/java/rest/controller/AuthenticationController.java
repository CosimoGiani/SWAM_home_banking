package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.spire.ms.System.Exception;

import dao.UserDao;
import model.User;
import otp.OneTimePasswordAuthenticator;
import utils.PasswordEncrypter;

@Model
public class AuthenticationController {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private OneTimePasswordAuthenticator oTPAuthenticator;
	
	public Long login(User loggingUser) { // OLD login
		User loggedUser = userDao.login(loggingUser);
		if( loggedUser == null ) {
			return null;
		} 
		return loggedUser.getId();
	}
	
	public User getUserFromEmail(String email) {
		User user = userDao.getUserFromEmail(email);
		if (user == null) {
			return null;
		}
		return user;
	}
	
	public User getUserFromCredentials(User userToAuthenticate) {
		User user = userDao.login(userToAuthenticate);
		if (user == null) {
			throw new Exception();
		} 
		return user;
	}
	
	public boolean checkCredentialsInDB(String username, String password) {
		return userDao.checkCredentials(username, PasswordEncrypter.encrypt(password));
	}
	
	public void generateOTP(String username, String password) {
		oTPAuthenticator.addUser(username, password);
	}
	
}
