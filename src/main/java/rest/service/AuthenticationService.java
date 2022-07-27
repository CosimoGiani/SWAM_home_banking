package rest.service;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import model.User;
import otp.OTPAuthenticated;
import rest.controller.AuthenticationController;
import utils.ParserJson;

@Path("auth")
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
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("login/get-otp")
	public Response getOtpFromCredentails(String credentials) {
		try {
			Map<String, String> credentialsData = ParserJson.fromString(credentials);
			String email = credentialsData.get("email");
			String password = credentialsData.get("password");

			if(authController.checkCredentialsInDB(email, password)) {
				authController.generateOTP(email, password);
				return Response.ok().entity("OTP generato con successo").build();
			} else {
				return Response.notAcceptable(null).entity("Credenziali non valide").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Errore nella generazione dell'OTP").build();
		}
	}
	
	
	@GET
	@Path("login/check-otp")
	@OTPAuthenticated
	public Response checkAuthenticationOtp(@HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
		    final String email = split[0];
			if(authController.isEmailInDB(email))
				return Response.ok("Utente autenticato con successo").build();
			else
				// entriamo qua solo se il filter OTPAuthenticated non funziona oppure c'è stata una cancellazione sul DB
				return Response.status(500).entity("Internal Error").build(); 
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Utente non autenticato").build();
		}
	}
	
	@GET
	@Path("logout")
	@OTPAuthenticated
	public Response removeOtp(@HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
		    final String email = split[0];
			authController.removeOTP(email);
			return Response.ok("Logout eseguito con successo").build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Session Error").build();
		}
	}
	
	@POST
	@Path("login/consultant")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginConsultant(String credentials) throws Exception {
		try {
			Map<String, String> credentialsData = ParserJson.fromString(credentials);
			String identificationNumber = credentialsData.get("identificationNumber");
			String password = credentialsData.get("password");
			
			if (authController.checkCredentialsConsultantInDB(identificationNumber, password)) {
				return Response.ok().entity("Consulente loggato con successo").build();
			} else {
				return Response.notAcceptable(null).entity("Credenziali non valide").build();
			}
		} catch(Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Credenziali non valide").build();
		}
	}
	
	@GET
	@Path("logout")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response logoutConsultant() {
		return Response.ok().entity("Consulente ha effettuato il logout").build();
	}

}
