package otp;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
	@Path("/auth/{email}")
	//@Produces("application/xml")
	@OTPAuthenticated
	public Response getUser(@PathParam("email") String email) {
		try {
			controller.getUserFromEmail(email);
			return Response.ok("Utente autenticato con successo").build();
		} catch (Exception e) {
			return Response.notAcceptable(null).entity("Utente non autenticato").build();
		}
	}
	
	@POST
	@Path("/addCreds/{email}")
	public Response addCreds(@PathParam("email") String email) {
		try {
			User user = controller.getUserFromEmail(email);
			app.updateMap(user);
			return Response.ok().entity("Credenziali aggiunte con successo").build();
		} catch (Exception e) {
			//e.printStackTrace();
			return Response.notAcceptable(null).entity("Credenziali non aggiunte").build();

		}
	}
	
}
