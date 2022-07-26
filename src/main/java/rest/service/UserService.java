package rest.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import model.BankAccount;

import javax.ws.rs.Produces;

import otp.OTPAuthenticated;
import rest.controller.UserController;

@Path("user")
public class UserService {
	
	@Inject 
	private UserController userController;

	@GET
	@Path("conti")
	@OTPAuthenticated
	@Produces(MediaType.APPLICATION_JSON)
	public List<?> getBankAccounts(@HeaderParam("Authorization") String authorization) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
	    
	    return userController.getAssociatedBankAccounts(email);
	}
}
