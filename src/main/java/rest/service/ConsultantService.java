package rest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.User;
import rest.controller.ConsultantController;
import utils.ParserJson;

@Path("consultant")
public class ConsultantService {
	
	@Inject
	private ConsultantController controller;
	
	@GET
	@Path("users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAssociatedUsers(String identificationNumber) {
		try {
			Map<String, String> idData = ParserJson.fromString(identificationNumber);
			return controller.getAssociatedUsers(idData.get("identificationNumber"), true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
