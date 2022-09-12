package pdf;

import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class PdfUtil {
	
	private String blank_file_path;
	private File pdf;
	private String upload_folder_path;
	private ExtractData extractor;
	
	public PdfUtil() throws IOException {
		
		extractor = new ExtractData();
		
		Path path = Paths.get("../standalone/deployments/contract.pdf").toAbsolutePath().normalize();
		blank_file_path = path.toString();
		
		pdf = new File(blank_file_path);
		
		path = Paths.get("../standalone/deployments/uploaded_files/").toAbsolutePath().normalize();
		upload_folder_path = path.toString();
		
		File uploadDir = new File(upload_folder_path);
		uploadDir.mkdir();
		
	}
	
	public File getPdf() {
		return this.pdf;
	}
	
	public Map<String, Object> extractData(InputStream uploadedInputStream) throws FileNotFoundException, IOException, NumberFormatException {
		
		String uploadedFileName = "/" + UUID.randomUUID().toString() + ".pdf";
		String uploadedFilePath = upload_folder_path + uploadedFileName;
		
	    int read = 0;  
	    byte[] bytes = new byte[1024];  
	    FileOutputStream out = new FileOutputStream(new File(uploadedFilePath));  
	    while ((read = uploadedInputStream.read(bytes)) != -1) {  
	        out.write(bytes, 0, read);  
	    }
	    out.flush();  
	    out.close();  
	    
	    Map<String, Object> data = extractor.extractData(uploadedFilePath);

		cleanUp(uploadedFilePath);
	    
	    return data;
	    
	}

	private void cleanUp(String uploadedFilePath) {
		File f = new File(uploadedFilePath);
	    f.delete();
	}
	
}
