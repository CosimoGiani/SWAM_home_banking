package rest.serviceTest;

import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BankAccount;
import model.User;
import model.enumeration.BankAccountType;

public class AuthenticationServiceTest extends ServiceTest {
	
	private User user;
	private String decryptedPassword;
	private BankAccount account;
	private String OTP;

	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/auth/");
		
		QueryUtils.queryTruncateAll(connection);
		
		decryptedPassword = "prova";
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", decryptedPassword);
		account = QueryUtils.queryCreateBankAccount(connection, "1234", (float) 230, "000011110000IT", BankAccountType.ORDINARIO);
		QueryUtils.queryLinkAccountToUser(connection, user, account);
		
	}
	
	@Test
	public void testGetOtpFromCredentails() {
		
		// TEST OTP generato con successo
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'email': '" + user.getEmail() + "', 'password': '"+decryptedPassword+"'}");
		
		Response response = executePost(request, "login/get-otp");
		response.then().statusCode(200);
		String body = response.getBody().asString();
		Assertions.assertEquals("OTP generato con successo", body);
		
		// TEST OTP non generato per via delle credenziali sbagliate
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'email': '" + user.getEmail() + "', 'password': 'wrongPassword'}");
			
		response = executePost(request, "login/get-otp");
		response.then().statusCode(406);
		body = response.getBody().asString();
		Assertions.assertEquals("Credenziali non valide", body);
		
		// TEST OTP non generato per via di errore nella formattazione della richiesta
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'email': 'test@test' 'password': 'any'}"); // manca una virgola
			
		response = executePost(request, "login/get-otp");
		response.then().statusCode(406);
		body = response.getBody().asString();
		Assertions.assertEquals("Errore nella formulazione delle richiesta", body);
		
		// TEST OTP resistenza a SQL Injection
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'email': ' 10\\\" OR \\\"1\\\"=\\\"1\\\" ', 'password': ' 10\\\" OR \\\"1\\\"=\\\"1\\\" ' }");
			
		response = executePost(request, "login/get-otp");
		response.then().statusCode(406);
		body = response.getBody().asString();
		Assertions.assertEquals("Credenziali non valide", body);
		
	}
	
	@Test
	public void testCheckAuthenticationOtp() {
		
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); // Caso con OTP corretto
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(200);
		body = response.getBody().asString();
		Assertions.assertEquals("Utente autenticato con successo", body);
		
		
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + "wrongOTP"); // Caso in cui l'OTP sia sbagliato
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(401);
		body = response.getBody().asString();
		Assertions.assertEquals("", body); // dato che è @OTPAuthenticatedStateful a bloccarci in questo, non ci ritorna nulla
	
		request = RestAssured.given();
		request.header("Authorization", "test@test.test " + OTP); // Caso in cui la mail sia sbagliata
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(401);
		body = response.getBody().asString();
		Assertions.assertEquals("", body); // dato che è @OTPAuthenticatedStateful a bloccarci in questo, non ci ritorna nulla
	
		
		
		try {
			QueryUtils.queryTruncateAll(connection);
			
			request = RestAssured.given();
			request.header("Authorization", user.getEmail() + " " + OTP); // Caso con OTP corretto e ancora attivo, ma con user rimosso dal DB (ERRORE INTERNO)
			
			response = executeGet(request, "login/check-otp");
			response.then().statusCode(500);
			body = response.getBody().asString();
			Assertions.assertEquals("Internal Error", body);
			
		} catch (SQLException e) {
			System.out.println("Errore nella cancellazione delle istanze dal DB");
		}
	}
	
	@Test
	public void testRemoveOTP() {
		
		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		RequestSpecification request;
		Response response;
		String body;
		
		
		// TEST LOGOUT non chiamabile con OTP sbagliato
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + "wrongOTP"); // OTP sbagliato 
		
		response = executeGet(request, "logout");
		response.then().statusCode(401);
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
		
		
		// TEST del LOGOUT
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); // OTP 
		
		response = executeGet(request, "logout");
		response.then().statusCode(200);
		body = response.getBody().asString();
		Assertions.assertEquals("Logout eseguito con successo", body);
		
		// controllo che OTP NON sia più valida
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); // offro l'OTP che avevo ricevuto durante il login
		
		response = executeGet(request, "login/check-otp");
		response.then().statusCode(401);
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
		
	}
	

}
