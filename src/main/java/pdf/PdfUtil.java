package pdf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

import com.spire.ms.System.Collections.Generic.List;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.fields.PdfField;
import com.spire.pdf.widget.PdfFormWidget;
import com.spire.pdf.widget.PdfRadioButtonListFieldWidget;
import com.spire.pdf.widget.PdfTextBoxFieldWidget;

import java.util.Date;

public class PdfUtil {
	private String file_path;
	private File pdf;
	private String upload_folder_path;
	
	public PdfUtil() throws IOException{
		
		Path path = Paths.get("../standalone/deployments/contract.pdf").toAbsolutePath().normalize();
		file_path = path.toString();
		// System.out.println(file_path);
		
		pdf = new File(file_path);
		
		path = Paths.get("../standalone/deployments/uploaded_files/").toAbsolutePath().normalize();
		upload_folder_path = path.toString();
		// System.out.println(upload_folder_path);
	}
	
	public File getPdf() {
		return this.pdf;
	}
	
	public boolean savePdf(InputStream uploadedInputStream) throws FileNotFoundException, IOException, ParseException {
		String fileName = UUID.randomUUID().toString() + ".pdf";
		String filePath = upload_folder_path + fileName;
		
	    int read = 0;  
	    byte[] bytes = new byte[1024];  
	    FileOutputStream out = new FileOutputStream(new File(filePath));  
	    while ((read = uploadedInputStream.read(bytes)) != -1) {  
	        out.write(bytes, 0, read);  
	    }
	    out.flush();  
	    out.close();  
	    
	    Map<String, Object> data = extractData(filePath);
	    
	    System.out.println(data);
	    
	    // TODO: aggiungere la tupla su db
	    
	    File f = new File(filePath);
	    f.delete();
	    
	    return true;
	}
	
	private Map<String, Object> extractData(String filePath) throws ParseException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		PdfDocument pdf = new PdfDocument();
		pdf.loadFromFile(filePath);
		
		PdfFormWidget formWidget = (PdfFormWidget)pdf.getForm();
		List<PdfField> fieldsList = formWidget.getFieldsWidget().getList();
		
		String name = ((PdfTextBoxFieldWidget)fieldsList.get(0)).getText();
		String surname = ((PdfTextBoxFieldWidget)fieldsList.get(1)).getText();
		
		if(containsDigit(name) || containsDigit(surname)) {
			pdf.close();
			File f = new File(filePath);
		    f.delete();
			throw new IllegalArgumentException("Name or Surname contains a number!");
		}
		
		String gg = ((PdfTextBoxFieldWidget)fieldsList.get(2)).getText();
		String mm = ((PdfTextBoxFieldWidget)fieldsList.get(3)).getText();
		String aaaa = ((PdfTextBoxFieldWidget)fieldsList.get(4)).getText();	   
		
		LocalDate birthDay = LocalDate.of(Integer.parseInt(aaaa), Integer.parseInt(mm), Integer.parseInt(gg));
		LocalDate currentDate = LocalDate.now();
		
		// Make sure the age is >= 18
		Period age = Period.between(birthDay, currentDate);
		if(age.getYears() < 18) {
			pdf.close();
			File f = new File(filePath);
		    f.delete();
			throw new IllegalArgumentException("You are too young to open a bank account!");
		}
		
		String city = ((PdfTextBoxFieldWidget)fieldsList.get(5)).getText();
		String province = ((PdfTextBoxFieldWidget)fieldsList.get(6)).getText();
		String address = ((PdfTextBoxFieldWidget)fieldsList.get(7)).getText();
		
		if(containsDigit(city) || containsDigit(province)) {
			pdf.close();
			File f = new File(filePath);
		    f.delete();
			throw new IllegalArgumentException("City or Province contains a number!");
		}
		
		String phone = ((PdfTextBoxFieldWidget)fieldsList.get(8)).getText();
		
		if(!isLegalPhoneNumber(phone)) {
			pdf.close();
			File f = new File(filePath);
		    f.delete();
			throw new IllegalArgumentException("Phone Number is not Valid!");
		}
		
		int nBankAccount = ((PdfRadioButtonListFieldWidget)fieldsList.get(9)).getSelectedIndex();
		String selectedBankAccount = "";
		
		if(nBankAccount == 0) {
			selectedBankAccount = "Ordinario";
		} else if (nBankAccount == 1) {
			selectedBankAccount = "Under30";
		} else if (nBankAccount == 2) {
			selectedBankAccount = "Investitore";
		}
		
		if(selectedBankAccount == "Under30" && age.getYears() > 30) {
			pdf.close();
			File f = new File(filePath);
		    f.delete();
			throw new IllegalArgumentException("You are too old to get an Under30 account!");
		}
		
		data.put("name", name);
		data.put("surname", surname);
		
		data.put("birthDate", birthDay); // <String, LocalDate>
		
		data.put("city", city);
		data.put("province", province);
		data.put("address", address);
		
		data.put("phone", phone);
		
		data.put("selectedBankAccount", selectedBankAccount);
				
		return data;
	}
	
	private boolean containsChar(String s) {
		char[] chars = s.toCharArray();
		for(char c : chars){
			if(!Character.isDigit(c)){
				return true;
			}
		}
		return false;
		
	}
	
	private boolean containsDigit(String s) {
		char[] chars = s.toCharArray();
		for(char c : chars){
			if(Character.isDigit(c)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isLegalPhoneNumber(String phone) {
		if(phone.length() == 10 && !containsChar(phone))
			return true;
		else {
			return false;
		}
	}
	
}
