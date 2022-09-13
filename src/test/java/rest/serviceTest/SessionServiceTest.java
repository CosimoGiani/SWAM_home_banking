package rest.serviceTest;

import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.User;

public class SessionServiceTest extends ServiceTest {
	
	private User user;
	private String decryptedPassword;

	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/session/");
		
		QueryUtils.queryTruncateAll(connection);
		
		decryptedPassword = "prova";
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", decryptedPassword);
		
	}
	
	@Test
	public void testSessionTransfer() {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		
		// Richiediamo il codice per trasferire la sessione
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		
		response = executeGet(request, "request-transfer");
		response.then().statusCode(200).contentType("application/json");
		String sessionCode = response.getBody().asString();
		Assertions.assertNotEquals("", sessionCode);
		
		// Dato il codice di trasferimento sessione richiediamo le credenziali
		request = RestAssured.given();
		request.header("Authorization", sessionCode); 
		
		response = executeGet(request, "get-transfer-credentials");
		response.then().statusCode(200).contentType("application/json");
		String credentials = response.getBody().asString();
		// System.out.println(credentials);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(credentials).getAsJsonObject();
		
		OTP = jsonObject.get("otp").getAsString();  // nuovo OTP
		String email = jsonObject.get("email").getAsString(); // solita email
		
		Assertions.assertEquals(user.getEmail(), email);
		
		// controlliamo che le nuove credenziali siano valide
		setBaseURL("/home.banking/api/auth/");
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(200);
		String body = response.getBody().asString();
		Assertions.assertEquals("Utente autenticato con successo", body);
	}
	
	@Test
	public void testWrongSessionTransfer() {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		
		// Richiediamo il codice per trasferire la sessione
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		
		response = executeGet(request, "request-transfer");
		response.then().statusCode(200).contentType("application/json");
		String sessionCode = response.getBody().asString();
		Assertions.assertNotEquals("", sessionCode);
		
		// Inseriamo un codice errato
		request = RestAssured.given();
		request.header("Authorization", "0000"); //codice errato
		
		response = executeGet(request, "get-transfer-credentials");
		response.then().statusCode(403);
		String body = response.getBody().asString();
		Assertions.assertEquals("", body); // body vuoto
	}

}
