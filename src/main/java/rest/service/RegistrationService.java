package rest.service;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

// import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
// import org.glassfish.jersey.media.multipart.FormDataParam;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

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
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response register(MultipartFormDataInput input) {

		try {
			InputStream uploadedInputStream = input.getFormDataPart("PDF", InputStream.class, null);
			String email = input.getFormDataPart("email", String.class, null);
		    String password = input.getFormDataPart("password", String.class, null);
		    String msg = registrationController.createAccount(uploadedInputStream, email, password);
			if (msg.equals("Account created successfully")) {
				return Response.status(200).entity(msg).build();
			} else if (msg.equals("Interal Error")) {
				return Response.status(500).entity(msg).build();
			} else
				return Response.notAcceptable(null).entity(msg).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(500).entity("Internal Error").build();
		}		
	}
}
