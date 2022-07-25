package otp;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import model.User;
import rest.controller.AuthenticationController;
import rest.service.RestApplicationConfig;

@Path("/otp")
public class UserResource {
	
	@Inject
	private AuthenticationController controller;
	
	@Inject
	private RestApplicationConfig app;

	@GET
	@Path("/auth/check-otp")
	@OTPAuthenticated
	public Response checkAuthenticationOtp(@HeaderParam("email") String email) {
		try {
			controller.getUserFromEmail(email);
			return Response.ok("Utente autenticato con successo").build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Utente non autenticato").build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("auth/get-otp")
	public Response getOtpFromCredentails(String credentials) throws Exception {
		Gson gson = new Gson();
		try {
			User user = controller.getUserFromCredentials(gson.fromJson(credentials, User.class));
			app.updateMap(user);
			return Response.ok().entity("OTP generato con successo").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Credenziali non valide").build();

		}
	}
	
}
