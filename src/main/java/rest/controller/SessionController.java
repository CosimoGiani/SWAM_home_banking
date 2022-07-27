package rest.controller;

import java.util.HashMap;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import session.SessionTransferManager;

@Model
public class SessionController {
	
	@Inject 
	private SessionTransferManager sessionTransferManager;
	
	public String requestSessionTransfer(String email, String otp) {
		return sessionTransferManager.requestSessionTransfer(email, otp);
	}
	
	public HashMap<String, String> getSessionCredentials(String sessionCode){
		return sessionTransferManager.getSessionCredentials(sessionCode);
	}
}
