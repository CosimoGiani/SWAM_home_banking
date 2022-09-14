package rest.service;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonSyntaxException;

import ConsultantAuthentication.AuthenticatedConsultant;
import rest.controller.ConsultantAuthenticationController;
import utils.ParserJson;

@Path("consultant")
public class ConsultantAuthenticationService {
	
	@Inject
	private ConsultantAuthenticationController authController;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("login")
	// @Produces({ "application/json", "text/plain" })
	public Response getAuthToken(String credentials) {
		try {
			Map<String, String> credentialsData = ParserJson.fromString(credentials);
			
			String identificationNumber = credentialsData.get("identificationNumber");
			String password = credentialsData.get("password");
			
			// System.out.println(identificationNumber);
			// System.out.println(password);
			
			if(authController.checkConsultantCredentials(identificationNumber, password)) {
				
				String token = authController.generateToken(identificationNumber);
				
				System.out.println("\nToken per il consulente "+ identificationNumber + " è attivo.\n");
				
				String response = "{ \n"
						+ "   'response': 'Autenticazione Riuscita', \n"
						+ "   'token': '" + token + "' \n"
						+"}";
				return Response.ok(response, MediaType.APPLICATION_JSON).build(); 
			} else {
				return Response.notAcceptable(null).entity("Credenziali non valide").build();
			}
		}  catch (JsonSyntaxException e) {
			return Response.notAcceptable(null).entity("Errore nella formulazione delle richiesta").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Errore nella generazione del token di accesso").build();
		}
		
	}
	
	@GET
	@Path("logout")
	@AuthenticatedConsultant
	public Response removeToken(@HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
		    final String identificationNumber = split[0];
			authController.removeToken(identificationNumber);
			return Response.ok("Logout eseguito con successo").build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Errore di sessione").build();
		}
	}
	
}
