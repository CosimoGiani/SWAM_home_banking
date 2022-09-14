package rest.serviceTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.User;

public class RegistrationServiceTest extends ServiceTest{
	
	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/registration/");
		
		QueryUtils.queryTruncateAll(connection);
		QueryUtils.queryCreateConsultant(connection, "000001", "Mario", "Bianchi", "password");
	}
	
	@Test
	public void testGetPdf() {
		
		RequestSpecification request;
		Response response;
		
		request = RestAssured.given();
		
		response = executeGet(request, "downloadPDF");
		response.then().statusCode(200).contentType("application/pdf");
		InputStream body = response.getBody().asInputStream();
		
		PdfDocument pdf = new PdfDocument();
		pdf.loadFromStream(body);
		PdfPageBase page = pdf.getPages().get(0);
		String text = page.extractText(); // estraiamo il testo del pdf
		// System.out.println(text);
		
		PdfDocument expectedPdf = new PdfDocument();
		expectedPdf.loadFromFile(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		PdfPageBase expectedPage = expectedPdf.getPages().get(0);
		String expectedText = expectedPage.extractText(); // estraiamo da un pdf di riferimento quello che il pdf scaricato deve contenere
		// System.out.println(expectedText); 
		
		Assertions.assertEquals(expectedText, text);
	}
	
	@Test
	public void testRegister() {
		// Caso in cui la registrazione avviene con successo
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(200);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Account created successfully", body);
		
		// Testiamo dunque che sia possibile ottenere l'OTP con questo nuovo account (dunque accedere)
		User user = mock(User.class);
		when(user.getEmail()).thenReturn(email);
		String OTP = OTPUtils.getOtp(user, password);
		
		Assertions.assertNotEquals("", OTP);
		
		// e dunque che l'OTP sia valido
		setBaseURL("/home.banking/api/auth/");
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(200);
		body = response.getBody().asString();
		Assertions.assertEquals("Utente autenticato con successo", body);
	}
	
	@Test
	public void testRegister_invalidEmail() {
		// Caso in cui la mail inserita non è una mail
		
		RequestSpecification request;
		Response response;
		
		String email = "test.test.com"; // manca @
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Invalid email", body);
	}
	
	@Test
	public void testRegister_shortPassword() {
		// Caso in cui la password inserita è troppo corta
		
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com"; 
		String password = "psw"; // psw troppo breve
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Password is too short", body);
	}
	
	@Test
	public void testRegister_emailUsed() throws SQLException {
		// Caso in cui si prova a registrare un nuovo utente con la stessa email
		
		String email = "test@test.com";
		String password = "test";
		
		QueryUtils.queryCreateUser(connection, email, "anyPassword"); // già presente un utente con questa mail
		
		RequestSpecification request;
		Response response;
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("There is already an account linked to this email", body);
	}
	
	@Test
	public void testRegister_wrongNameSurname() {
		// Caso in cui l'utente inserisce nome o cognome contentente un numero
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf con nome "Mario10"
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test-name.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Name or Surname contains a number!", body);
	}
	
	@Test
	public void testRegister_tooYoung() {
		// Caso in cui l'utente che richiede la generazione è minorenne 
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf con data di nascita di un minorenne
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test-age.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("You are too young to open a bank account!", body);
	}
	
	@Test
	public void testRegister_numberInCityProvince() {
		// Caso in cui l'utente ha inserito un numero nel campo Provincia o Città
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf con città "Prato10"
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test-city.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("City or Province contains a number!", body);
	}
	
	@Test
	public void testRegister_wrongPhoneNumber() {
		// Caso in cui l'utente ha inserito un carattere nel numero di telefono 
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf con numero di telefono che contiene una "c"
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test-phone.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Phone Number is not Valid!", body);
	}
	
	@Test
	public void testRegister_under30() {
		// Caso in cui l'utente richiede un conto under30 ma ha più di 30 anni
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf con richiesta di apertura di un under30 per una persona che ha più di 30 anni
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test-under30.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("You are too old to get an Under30 account!", body);
	}
	
	@Test
	public void testRegister_wrongDocumentUploaded() throws SQLException {
		// Caso in cui viene caricato un .pdf che non è il form della banca
		
		RequestSpecification request;
		Response response;
		String body;
		File fileToUpload;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		
		// Carico pdf che non è quello della banca (che presenta dei form-fields)
		fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-wrong-1.pdf").toAbsolutePath().normalize().toString());
		
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		body = response.getBody().asString();
		
		Assertions.assertEquals("The uploaded document is incorrect", body);
		
		// Carico pdf che non è quello della banca (senza form fields)
		fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-wrong-2.pdf").toAbsolutePath().normalize().toString());
				
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		body = response.getBody().asString();
		
		Assertions.assertEquals("The uploaded document is incorrect", body);
		
		// Carico un file .txt invece che un .pdf
		fileToUpload = new File(Paths.get("../home.banking/src/test/resources/not-a-pdf.txt").toAbsolutePath().normalize().toString());
				
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		body = response.getBody().asString();
		
		Assertions.assertEquals("The uploaded document is incorrect", body);
	}
	
	@Test
	public void testRegister_wrongRequestFormat() throws SQLException {
		// Caso in cui viene presentata una richiesta errata dei formParams
		
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("eml", email)            // scrittura errata del campo email (campo "email" mancante)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Request not acceptable", body);
	}
	
	
	@Test
	public void testRegister_noConsultantAvailable() throws SQLException {
		// Caso in cui nessun Consulente è disponibile in fase di registrazione
		// (non dovrebbe mai accadere in pratica)
		
		QueryUtils.queryTruncateAll(connection); // per assicurarci che non ci sia alcun Consulente nel DB
		
		RequestSpecification request;
		Response response;
		
		String email = "test@test.com";
		String password = "test";
		
		request = RestAssured.given();
		request.header("Content-Type", "multipart/form-data");
		File fileToUpload = new File(Paths.get("../home.banking/src/test/resources/pdf-test.pdf").toAbsolutePath().normalize().toString());
		request.multiPart("PDF", fileToUpload)
			.formParam("email", email)
			.formParam("password", password);
		
		response = executePost(request, "send");
		response.then().statusCode(500);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Internal Error", body);
	}

}
