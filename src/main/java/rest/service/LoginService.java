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
import rest.controller.LoginController;

@Path("log")
public class LoginService {
	
	@Inject
	private LoginController loginController;
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces( { "application/json", "text/plain" } )
	public Response login(String request) throws Exception {
		Gson gson = new Gson();
		try {
			Long response = loginController.login(gson.fromJson(request, User.class));
			Response resp = Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
			return resp;
		} catch(Exception e) {
			return Response.notAcceptable(null).build();
		}
	}

}
