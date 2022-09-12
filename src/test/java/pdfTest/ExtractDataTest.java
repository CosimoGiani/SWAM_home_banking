package pdfTest;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import pdf.ExtractData;

public class ExtractDataTest {
	
	private ExtractData data;
	
	@BeforeEach
	public void setup() {
		data = new ExtractData();
	}
	
	@Test
	public void testExtractData() {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		
		Map<String, Object> extractedData = new HashMap<String, Object>();
		extractedData.put("name", "Mario");
		extractedData.put("surname", "Rossi");
		extractedData.put("birthDate", LocalDate.of(Integer.parseInt("1995"), Integer.parseInt("07"), Integer.parseInt("25")));
		extractedData.put("address", "ciccio");
		extractedData.put("city", "prato");
		extractedData.put("province", "parma");
		extractedData.put("phone", "3349959603");
		extractedData.put("selectedBankAccount", "Under30");
		
		assertEquals(extractedData, data.extractData(path));
	}
	
	@Test
	public void testExtractDataNameWithDigits() throws IOException {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test-name.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		File source = new File(path);
		Path destDirectory = Paths.get("../home.banking/src/test/resources/pdf-test-name-copy.pdf").toAbsolutePath().normalize();
		String pathDest = destDirectory.toString();
		File dest = new File(pathDest);
		copyFile(source, dest);
		assertThrows(IllegalArgumentException.class, () -> {
			data.extractData(pathDest);
		});
	}
	
	@Test
	public void testExtractDataAgeTooYoung() throws IOException {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test-age.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		File source = new File(path);
		Path destDirectory = Paths.get("../home.banking/src/test/resources/pdf-test-age-copy.pdf").toAbsolutePath().normalize();
		String pathDest = destDirectory.toString();
		File dest = new File(pathDest);
		copyFile(source, dest);
		assertThrows(IllegalArgumentException.class, () -> {
			data.extractData(pathDest);
		});
	}
	
	@Test
	public void testExtractDataCityWithDigits() throws IOException {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test-city.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		File source = new File(path);
		Path destDirectory = Paths.get("../home.banking/src/test/resources/pdf-test-city-copy.pdf").toAbsolutePath().normalize();
		String pathDest = destDirectory.toString();
		File dest = new File(pathDest);
		copyFile(source, dest);
		assertThrows(IllegalArgumentException.class, () -> {
			data.extractData(pathDest);
		});
	}
	
	@Test
	public void testExtractDataPhoneNumberIsLegal() throws IOException {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test-phone.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		File source = new File(path);
		Path destDirectory = Paths.get("../home.banking/src/test/resources/pdf-test-phone-copy.pdf").toAbsolutePath().normalize();
		String pathDest = destDirectory.toString();
		File dest = new File(pathDest);
		copyFile(source, dest);
		assertThrows(IllegalArgumentException.class, () -> {
			data.extractData(pathDest);
		});
	}
	
	@Test
	public void testExtractDataTooOldForUnder30Account() throws IOException {
		Path directory = Paths.get("../home.banking/src/test/resources/pdf-test-under30.pdf").toAbsolutePath().normalize();
		String path = directory.toString();
		File source = new File(path);
		Path destDirectory = Paths.get("../home.banking/src/test/resources/pdf-test-under30-copy.pdf").toAbsolutePath().normalize();
		String pathDest = destDirectory.toString();
		File dest = new File(pathDest);
		copyFile(source, dest);
		assertThrows(IllegalArgumentException.class, () -> {
			data.extractData(pathDest);
		});
	}
	
	private void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}

}
