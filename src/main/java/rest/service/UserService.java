package rest.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import model.BankAccount;
import model.Consultant;
import model.User;

import javax.ws.rs.Produces;

import otp.OTPAuthenticated;
import otpStateful.OTPAuthenticatedStateful;
import rest.controller.UserController;

@Path("user")
public class UserService {
	
	@Inject 
	private UserController userController;
	
	@GET
	@Path("accounts")
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	@Produces(MediaType.APPLICATION_JSON)
	public List<BankAccount> getBankAccounts(@HeaderParam("Authorization") String authorization) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
	    
	    return userController.getAssociatedBankAccounts(email);
	}
	
	@GET
	@Path("personal-data")
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	@Produces(MediaType.APPLICATION_JSON)
	public User getPersonalData(@HeaderParam("Authorization") String authorization) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
	    
	    return userController.getUserFromEmail(email, true);
	}
	
	
	@GET
	@Path("consultant-data")
	@OTPAuthenticatedStateful
	@Produces(MediaType.APPLICATION_JSON)
	public Consultant getConsultantData(@HeaderParam("Authorization") String authorization) {
		/* Get info del Consultant */
		
		String[] split = authorization.split(" ");
	    final String email = split[0];
	
	    return userController.getConsultantAssociated(email);
	}
	
	// Richiesta Consulenza (come una sorta di messaggio, che poi in realtà sarà una email)
	

}
