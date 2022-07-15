package rest.service;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import pdf.PdfUtil;

@Path("pdf")
@RequestScoped
public class PdfService {
	
	@GET
	@Produces("application/pdf")
	public Response getPdf() {
		try {
			PdfUtil pdfUtil = new PdfUtil();
			File pdf = pdfUtil.getPdf();
			ResponseBuilder response = Response.ok((Object) pdf);  
	        response.header("Content-Disposition", "attachment; filename=\"home-banking.pdf\"");  
			return response.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.notAcceptable(null).build();
		}
	}
	

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadPdf(
			@FormDataParam("File") InputStream uploadedInputStream) {
			// @FormDataParam("File") FormDataContentDisposition fileDetail) {
		   
		try {  
			PdfUtil pdfUtil = new PdfUtil();
			// pdfUtil.savePdf(uploadedInputStream, fileDetail);
			pdfUtil.savePdf(uploadedInputStream);
			return Response.status(200).entity("File successfully uploaded").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).build();
		}
	}
}
