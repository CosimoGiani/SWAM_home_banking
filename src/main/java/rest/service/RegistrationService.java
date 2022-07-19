package rest.service;

import java.io.InputStream;
import java.io.File;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import rest.controller.RegistrationController;

@Path("registration")
public class RegistrationService {
	
	@Inject
	private RegistrationController registrationController;
	
	@GET
	@Path("downloadPDF")
	@Produces("application/pdf")
	public Response getPdf() { 
		File pdf = registrationController.getPdf();
		ResponseBuilder response = Response.ok((Object) pdf);  
        response.header("Content-Disposition", "attachment; filename=\"home-banking.pdf\"");  
		return response.build();
	}
	

	@POST
	@Path("send")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	public Response register(
			@FormDataParam("PDF") InputStream uploadedInputStream,
			@FormDataParam("PDF") FormDataContentDisposition tmp,
			@FormDataParam("email") String email,
			@FormDataParam("password") String password) {
		
		
		
		return registrationController.createAccount(uploadedInputStream, email, password);
	}
}
