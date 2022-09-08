package rest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.BankAccount;
import model.Consultant;
import model.User;

import javax.ws.rs.Produces;

// import otp.OTPAuthenticated;
import otpStateful.OTPAuthenticatedStateful;
import rest.controller.UserController;
import utils.ParserJson;

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
		String userEmail = split[0];
	    
	    return userController.getAssociatedBankAccounts(userEmail);
	}
	
	@GET
	@Path("personal-data")
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	@Produces(MediaType.APPLICATION_JSON)
	public User getPersonalData(@HeaderParam("Authorization") String authorization) {
		String[] split = authorization.split(" ");
		String userEmail = split[0];
	    
	    return userController.getUserFromEmail(userEmail, true);
	}
	
	@GET
	@Path("consultant-data")
	@OTPAuthenticatedStateful
	@Produces(MediaType.APPLICATION_JSON)
	public Consultant getConsultantData(@HeaderParam("Authorization") String authorization) {
		/* Get info del Consultant */
		
		String[] split = authorization.split(" ");
		String userEmail = split[0];
	
	    return userController.getConsultantAssociated(userEmail);
	}
	
	@POST
	@Path("send-to-consultant")
	@OTPAuthenticatedStateful
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageToConsultant(@HeaderParam("Authorization") String authorization, String messageContent) {
		String[] split = authorization.split(" ");
	    String userEmail = split[0];
	    
	    Map<String, String> messageData = ParserJson.fromString(messageContent);
	    String object = messageData.get("object");
	    String corpus = messageData.get("corpus");
	    
	    if(object != null && corpus != null) {
	    	String msg = userController.sendMessageToConsultant(userEmail, object, corpus);
	    	return Response.ok(msg).build();
	    } else {
	    	return Response.status(400).entity("La formulazione del messaggio non è corretta").build();
	    }
	    
	}

}
