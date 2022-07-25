package rest.service;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import model.User;
import rest.controller.AuthenticationController;

@Path("login")
public class AuthenticationService {
	
	@Inject
	private AuthenticationController authController;
	
	@POST
	@Path("loginOLD")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces( { "application/json", "text/plain" } )
	public Response login(String request) throws Exception {
		Gson gson = new Gson();
		try {
			Long response = authController.login(gson.fromJson(request, User.class));
			Response resp = Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
			return resp;
		} catch(Exception e) {
			return Response.notAcceptable(null).build();
		}
	}
	
	/*
	@POST
	@Path("credentials")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticateUserWithCredentials(String credentials) throws Exception {
		Gson gson = new Gson();
		try {
			String otp = authController.authenticate(gson.fromJson(credentials, User.class));
			// DA TOGLIERE L'OTP DENTRO IL RESPONSE, PER TESTARE PER ORA SI TIENE
			return Response.ok(otp).build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Credenziali errate").build();
		}
	}
	
	@POST
	@Path("otp")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticateUserWithOTP(String otp) {
		try {
			SessionOTP bean = session.checkOtp(otp);
			return Response.ok(bean).build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("OTP inserito è invalido").build();
		}
	}*/
	

}
