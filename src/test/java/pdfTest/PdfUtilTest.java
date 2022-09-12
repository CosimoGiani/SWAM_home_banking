package pdfTest;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import pdf.ExtractData;
import pdf.PdfUtil;

public class PdfUtilTest {
	
	private PdfUtil pdfUtil;
	private ExtractData extractor;
	private Map<String, Object> extractedData;
		
	@Test
	public void testExtractData() throws IOException, IllegalAccessException {
		
		pdfUtil = new PdfUtil();
		
		InputStream input = PdfUtilTest.class.getResourceAsStream("/pdf-test.pdf");
		
		extractedData = new HashMap<String, Object>();
		extractedData.put("name", "Mario");
		extractedData.put("surname", "Rossi");
		extractedData.put("birthDate", LocalDate.of(Integer.parseInt("1995"), Integer.parseInt("07"), Integer.parseInt("25")));
		extractedData.put("address", "ciccio");
		extractedData.put("city", "prato");
		extractedData.put("province", "parma");
		extractedData.put("phone", "3349959603");
		extractedData.put("selectedBankAccount", "Under30");
		
		Path path = Paths.get("../standalone/").toAbsolutePath().normalize();
		String test_folder_path = path.toString();
		File testDir = new File(test_folder_path);
		testDir.mkdir();
		
		path = Paths.get("../standalone/deployments/").toAbsolutePath().normalize();
		test_folder_path = path.toString();
		testDir = new File(test_folder_path);
		testDir.mkdir();
		
		path = Paths.get("../standalone/deployments/uploaded_files/").toAbsolutePath().normalize();
		test_folder_path = path.toString();
		testDir = new File(test_folder_path);
		testDir.mkdir();
		
		extractor = mock(ExtractData.class);
		when(extractor.extractData(test_folder_path)).thenReturn(extractedData);
		Map<String, Object> data = pdfUtil.extractData(input);
		assertEquals(extractedData, data);
		
		assertTrue(isDirEmpty(path));
	}
	
	private static boolean isDirEmpty(final Path directory) throws IOException {
	    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
	        return !dirStream.iterator().hasNext();
	    }
	}

}
