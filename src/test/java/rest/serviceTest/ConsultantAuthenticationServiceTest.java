package rest.serviceTest;

import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Consultant;

public class ConsultantAuthenticationServiceTest extends ServiceTest {
	
	private String decryptedPassword;
	private Consultant consultant;

	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/consultant/");
		
		QueryUtils.queryTruncateAll(connection);
		decryptedPassword = "test";
		consultant = QueryUtils.queryCreateConsultant(connection, "000001", "Mario", "Bianchi", decryptedPassword);

	}
	
	@Test
	public void testLogin() {
		
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'identificationNumber': '" + consultant.getIdentificationNumber() + "', 'password': '"+decryptedPassword+"'}");
		
		Response response = executePost(request, "login");
		response.then().statusCode(200).contentType("application/json");
		String body = response.getBody().asString();
		// System.out.println(body);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonBody = parser.parse(body).getAsJsonObject();
		
		String token = jsonBody.get("token").getAsString();
		Assertions.assertEquals("Autenticazione Riuscita", jsonBody.get("response").getAsString());
		Assertions.assertNotEquals("", token);
		
		// System.out.println(token);
	}
	
	@Test
	public void testLogin_wrongCredentials() {
		// caso in cui si usino delle credenziali non valide
		
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'identificationNumber': '" + consultant.getIdentificationNumber() + "', 'password': 'wrongPassword'}"); // password sbagliata
		
		Response response = executePost(request, "login");
		response.then().statusCode(406);
		String body = response.getBody().asString();
		
		Assertions.assertEquals("Credenziali non valide", body);
	}
	
	@Test
	public void testLogin_wrongRequestFormat() {
		// caso in cui vi sia un errore nella request
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		// "username" al posto di "identificationNumber"
		request.body("{'username': '" + consultant.getIdentificationNumber() + "', 'password': '"+decryptedPassword+"'}");
		
		response = executePost(request, "login");
		response.then().statusCode(406);
		body = response.getBody().asString();
		
		Assertions.assertEquals("Credenziali non valide", body);
		
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		// json mal formattato (manca una virgola)
		request.body("{'identificationNumber': '" + consultant.getIdentificationNumber() + "' 'password': '"+decryptedPassword+"'}");
		
		response = executePost(request, "login");
		response.then().statusCode(406);
		body = response.getBody().asString();
		
		Assertions.assertEquals("Errore nella formulazione delle richiesta", body);
	}
	
	@Test
	public void testLogout() {
		
		// Faccio il login 
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'identificationNumber': '" + consultant.getIdentificationNumber() + "', 'password': '"+decryptedPassword+"'}");
		
		response = executePost(request, "login");
		response.then().statusCode(200).contentType("application/json");
		body = response.getBody().asString();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonBody = parser.parse(body).getAsJsonObject();
		
		String token = jsonBody.get("token").getAsString();
		Assertions.assertEquals("Autenticazione Riuscita", jsonBody.get("response").getAsString());
		Assertions.assertNotEquals("", token);
		
		
		// Caso in cui si cerca di fare il logout sbagliando token o matricola
		request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + "wrongToken");
		
		response = executeGet(request, "logout");
		response.then().statusCode(401);
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
		
	
		// Logout che avviene con successo
		request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + token);
		
		response = executeGet(request, "logout");
		response.then().statusCode(200);
		body = response.getBody().asString();
		Assertions.assertEquals("Logout eseguito con successo", body);
		
	}
	
}
