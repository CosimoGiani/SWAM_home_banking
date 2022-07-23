package rest.controller;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.spire.ms.System.Exception;

import dao.UserDao;
import model.User;

@Model
public class AuthenticationController {
	
	@Inject
	private UserDao userDao;
	
	public Long login(User loggingUser) {
		User loggedUser = userDao.login(loggingUser);
		if( loggedUser == null ) {
			return null;
		} 
		return loggedUser.getId();
	}
	
	public String authenticate(User userToAuthenticate) {
		User user = userDao.login(userToAuthenticate);
		if(user == null) {
			throw new Exception();
		} 
		String otp = generateOTP();
		//session.setOtp(otp);
		sendOtpViaEmail(userToAuthenticate.getEmail(), otp);
		return otp;
	}
	
	private String generateOTP() {
		Random random = new SecureRandom();
		String otp = new BigInteger(130, random).toString(32);
		return otp;
	}
	
	private void sendOtpViaEmail(String email, String otp) {
		System.out.println("==============================================================");
		System.out.println("Il codice OTP è: " + otp);
		System.out.println("ed è stato inviato alla seguente mail: " + email);
		System.out.println("==============================================================");
	}
	
	public User getUserFromEmail(String email) {
		User user = userDao.getUserFromEmail(email);
		if (user == null) {
			return null;
		}
		return user;
	}
	
}
