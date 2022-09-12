package pdf;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import com.spire.ms.System.Collections.Generic.List;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.widget.PdfFormWidget;
import com.spire.pdf.widget.PdfRadioButtonListFieldWidget;
import com.spire.pdf.widget.PdfTextBoxFieldWidget;

public class ExtractData {
	
	public Map<String, Object> extractData(String uploadedFilePath) throws NumberFormatException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		PdfDocument pdf = new PdfDocument();
		pdf.loadFromFile(uploadedFilePath);
		
		PdfFormWidget formWidget = (PdfFormWidget)pdf.getForm();
		List<?> fieldsList = (List<?>) formWidget.getFieldsWidget().getList();
				
		String name = ((PdfTextBoxFieldWidget)fieldsList.get(0)).getText();
		String surname = ((PdfTextBoxFieldWidget)fieldsList.get(1)).getText();
		
		if(containsDigit(name) || containsDigit(surname)) {
			pdf.close();
			cleanUp(uploadedFilePath);
			throw new IllegalArgumentException("Name or Surname contains a number!");
		}
		
		String gg = ((PdfTextBoxFieldWidget)fieldsList.get(3)).getText(); // as there is a mismatch in the pdf
		String mm = ((PdfTextBoxFieldWidget)fieldsList.get(4)).getText();
		String aaaa = ((PdfTextBoxFieldWidget)fieldsList.get(5)).getText();	   
		
		LocalDate birthDay = LocalDate.of(Integer.parseInt(aaaa), Integer.parseInt(mm), Integer.parseInt(gg));
		LocalDate currentDate = LocalDate.now();
		
		// Make sure the age is >= 18
		Period age = Period.between(birthDay, currentDate);
		if(age.getYears() < 18) {
			pdf.close();
			cleanUp(uploadedFilePath);
			throw new IllegalArgumentException("You are too young to open a bank account!");
		}
		
		String city = ((PdfTextBoxFieldWidget)fieldsList.get(2)).getText(); // as there is a mismatch in the pdf
		String province = ((PdfTextBoxFieldWidget)fieldsList.get(6)).getText();
		String address = ((PdfTextBoxFieldWidget)fieldsList.get(7)).getText();
		
		if(containsDigit(city) || containsDigit(province)) {
			pdf.close();
			cleanUp(uploadedFilePath);
			throw new IllegalArgumentException("City or Province contains a number!");
		}
		
		String phone = ((PdfTextBoxFieldWidget)fieldsList.get(8)).getText();
		
		if(!isLegalPhoneNumber(phone)) {
			pdf.close();
			cleanUp(uploadedFilePath);
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
			cleanUp(uploadedFilePath);
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
	
	private void cleanUp(String uploadedFilePath) {
		File f = new File(uploadedFilePath);
	    f.delete();
	}

}
