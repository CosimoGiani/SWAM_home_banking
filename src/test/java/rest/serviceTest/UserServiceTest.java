package rest.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BankAccount;
import model.Consultant;
import model.User;
import model.enumeration.BankAccountType;

public class UserServiceTest extends ServiceTest {
	
	private User user;
	private BankAccount account1;
	private BankAccount account2;
	private String decryptedPassword;
	private String OTP;
	private Consultant consultant;
	
	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/user/");
		
		QueryUtils.queryTruncateAll(connection);
		
		decryptedPassword = "prova";
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", decryptedPassword);
		account1 = QueryUtils.queryCreateBankAccount(connection, "1234", (float) 230, "000011110000IT", BankAccountType.ORDINARIO);
		account2 = QueryUtils.queryCreateBankAccount(connection, "0011", (float) 150, "000011110000IT", BankAccountType.INVESTITORE);
		consultant = QueryUtils.queryCreateConsultant(connection, "123456", "Mario", "Rossi", "password");
		QueryUtils.queryLinkUserToConsultant(connection, consultant, user);
		QueryUtils.queryLinkAccountToUser(connection, user, account1);
		QueryUtils.queryLinkAccountToUser(connection, user, account2);
		
	}
	
	@Test
	public void testGetBankAccounts() {
		
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		response = executeGet(request, "accounts");
		response.then().statusCode(200).contentType("application/json");
		String text = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonArray arr = parser.parse(text).getAsJsonArray();
		JsonObject obj1 = arr.get(0).getAsJsonObject();
		JsonObject obj2 = arr.get(1).getAsJsonObject();
		
		assertEquals(obj1.get("uuid").getAsString(), account1.getUuid());
		assertEquals(obj1.get("accountNumber").getAsString(), account1.getAccountNumber());
		assertEquals(obj1.get("balance").getAsFloat(), account1.getBalance());
		assertEquals(obj1.get("iban").getAsString(), account1.getIban());
		assertEquals(obj1.get("type").getAsString(), account1.getType().toString());
		
		assertEquals(obj2.get("uuid").getAsString(), account2.getUuid());
		assertEquals(obj2.get("accountNumber").getAsString(), account2.getAccountNumber());
		assertEquals(obj2.get("balance").getAsFloat(), account2.getBalance());
		assertEquals(obj2.get("iban").getAsString(), account2.getIban());
		assertEquals(obj2.get("type").getAsString(), account2.getType().toString());
	}
	
	@Test
	public void testGetBankAccountsWhenUserHasNoAccounts() throws SQLException {
		User testUser = QueryUtils.queryCreateUser(connection, "prova@example.com", "test");
		OTP = OTPUtils.getOtp(testUser, "test");
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testUser.getEmail() + " " + OTP);
		response = executeGet(request, "accounts");
		response.then().statusCode(204);
		String body = response.getBody().asString();
		Assertions.assertEquals("", body);
	}
	
	@Test
	public void testGetBankAccountsWhenAnotherUserRequests() throws SQLException {
		// testo che non sia possibile per un utente recuperare gli accounts di un altro utente
		User testUser = QueryUtils.queryCreateUser(connection, "prova@example.com", "test");
		BankAccount testAccount = QueryUtils.queryCreateBankAccount(connection, "1111", (float) 400, "000011120000IT", BankAccountType.ORDINARIO);
		QueryUtils.queryLinkAccountToUser(connection, testUser, testAccount);
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		response = executeGet(request, "accounts");
		response.then().statusCode(200).contentType("application/json");
		String text = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonArray arr = parser.parse(text).getAsJsonArray();
		JsonObject obj1 = arr.get(0).getAsJsonObject();
		JsonObject obj2 = arr.get(1).getAsJsonObject();
		
		// verifico che nessun account recuperato dalla get dello user sia quello dello testUser
		assertNotEquals(obj1.get("uuid").getAsString(), testAccount.getUuid());
		assertNotEquals(obj1.get("accountNumber").getAsString(), testAccount.getAccountNumber());
		
		assertNotEquals(obj2.get("uuid").getAsString(), testAccount.getUuid());
		assertNotEquals(obj2.get("accountNumber").getAsString(), testAccount.getAccountNumber());
		
	}
	
	@Test
	public void testGetPersonalData() {
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		response = executeGet(request, "personal-data");
		response.then().statusCode(200).contentType("application/json");
		String text = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(text).getAsJsonObject();

		assertEquals(user.getUuid(), obj.get("uuid").getAsString());
		assertEquals(user.getEmail(), obj.get("email").getAsString());
	}
	
	@Test
	public void testGetConsultantData() throws SQLException {
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		response = executeGet(request, "consultant-data");
		response.then().statusCode(200).contentType("application/json");
		String text = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(text).getAsJsonObject();
		
		assertEquals(consultant.getUuid(), obj.get("uuid").getAsString());
		assertEquals(consultant.getFirstname(), obj.get("firstname").getAsString());
		assertEquals(consultant.getIdentificationNumber(), obj.get("identificationNumber").getAsString());
		assertEquals(consultant.getLastname(), obj.get("lastname").getAsString());
	}
	
	@Test
	public void testSendMessageToConsultant() {
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{ \"object\" : \"Richiesta appuntamento\", \"corpus\": "
				+ "\"Buongiorno, richiedo un appuntamento per la prossima settimana così da ricevere dei consigli per quanto "
				+ "riguarda un investimento. \\n -Saluti\" }");
		response = executePost(request, "send-to-consultant");
		response.then().statusCode(200);
		String text = response.getBody().asString();
		assertEquals("Messaggio inviato correttamente", text);
	}
	
	@Test
	public void testSendMessageToConsultantWhenCorpusIsMisspelled() {
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{ \"object\" : \"Richiesta appuntamento\", \"coprus\": \"\"}");
		response = executePost(request, "send-to-consultant");
		response.then().statusCode(400);
		String text = response.getBody().asString();
		assertEquals("La formulazione del messaggio non è corretta", text);
	}
	
	@Test
	public void testSendMessageToConsultantWhenMessageRequestIsInvalid() {
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		Response response;
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{ \"object\" : \"Richiesta appuntamento\", \"corpus\": }");
		response = executePost(request, "send-to-consultant");
		response.then().statusCode(500);
		String text = response.getBody().asString();
		assertEquals("Errore nella formulazione della richiesta", text);
	}

}
