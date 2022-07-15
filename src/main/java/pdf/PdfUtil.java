package pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


public class PdfUtil {
	private String file_path;
	private File pdf;
	
	public PdfUtil() throws IOException{
		
		Path path = Paths.get("../standalone/deployments/contract.pdf").toAbsolutePath().normalize();
		file_path = path.toString();
		// System.out.println(file_path);
		
		pdf = new File(file_path);
	}
	
	public File getPdf() {
		return this.pdf;
	}
	
	public boolean savePdf(InputStream uploadedInputStream) throws FileNotFoundException, IOException {
		//String fileLocation = "tmp/" + fileDetail.getFileName();
		String fileLocation = "file.pdf";
		
		FileOutputStream out = new FileOutputStream(new File(fileLocation));  
	    int read = 0;  
	    byte[] bytes = new byte[1024];  
	    out = new FileOutputStream(new File(fileLocation));  
	    while ((read = uploadedInputStream.read(bytes)) != -1) {  
	        out.write(bytes, 0, read);  
	    }  
	    out.flush();  
	    out.close();  
	    return true;
	}
	
	public boolean savePdf(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) throws FileNotFoundException, IOException {
		String fileLocation = "tmp/" + fileDetail.getFileName();
		
		FileOutputStream out = new FileOutputStream(new File(fileLocation));  
	    int read = 0;  
	    byte[] bytes = new byte[1024];  
	    out = new FileOutputStream(new File(fileLocation));  
	    while ((read = uploadedInputStream.read(bytes)) != -1) {  
	        out.write(bytes, 0, read);  
	    }  
	    out.flush();  
	    out.close();  
	    return true;
	}
	
}
