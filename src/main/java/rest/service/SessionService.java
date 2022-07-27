package rest.service;

import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import otp.OTPAuthenticated;
import rest.controller.SessionController;

@Path("session")
public class SessionService {
	
	@Inject
	private SessionController sessionController;
	
	@GET
	@Path("request-transfer")
	@OTPAuthenticated
	@Produces(MediaType.APPLICATION_JSON)
	public String requestSessionTransfer(@HeaderParam("Authorization") String authorization) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
	    final String otp = split[1];
	    
	    return sessionController.requestSessionTransfer(email, otp);
	}
	
	@GET
	@Path("get-transfer-credentials")
	public Response getSessionCredentials(@HeaderParam("Authorization") String sessionCode) {
		HashMap<String, String> sessionCredentials = sessionController.getSessionCredentials(sessionCode);
		if(sessionCredentials == null) {
			// System.out.println("Nessuna sessione con questo codice");
			return Response.status(403).build();
		} else {
			return Response.ok(sessionCredentials, MediaType.APPLICATION_JSON).build();
		}
		
	}
	
	
}
