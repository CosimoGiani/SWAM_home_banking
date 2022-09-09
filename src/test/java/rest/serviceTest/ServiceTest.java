package rest.serviceTest;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class ServiceTest {
	
	private String baseURL;   //  "/home.banking/api/..."
	
	private static String DBconnectionURL;
	private static String username;
	private static String password;
	
	private static String deploymentsPath;
	
	protected static Connection connection;
	
	@BeforeAll
	public static void setup() throws SQLException {
		JsonParser parser = new JsonParser();
		Reader json = new InputStreamReader(ServiceTest.class.getResourceAsStream("/configurations.json"));
		
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		
		JsonObject rest =  jsonObject.get("rest").getAsJsonObject();
		String baseURI  =  rest.get("baseURI").getAsString();
		int port        =  rest.get("port").getAsInt();
		
		RestAssured.baseURI = baseURI;
		RestAssured.port = port;
		
		RestAssured.get().then().statusCode(200); // controlla se il server è online ancora prima di iniziare i tests veri e propri
		
		
		JsonObject DB    =  jsonObject.get("DB").getAsJsonObject();
		DBconnectionURL  =  DB.get("connectionURL").getAsString();
		username         =  DB.get("username").getAsString();
		password         =  DB.get("password").getAsString();
		
		connection = DriverManager.getConnection(DBconnectionURL, username, password); // controlla se il DB è raggiungibile
		
		deploymentsPath = jsonObject.get("deploymentsPath").getAsString();
		
		System.out.println("Configuration file loaded correctly");
	}
	
	@BeforeEach
	public void beforeEach() throws IllegalAccessException, SQLException {
		
		beforeEachInit();
	}
	
	protected abstract void beforeEachInit() throws SQLException;
	
	@AfterEach
    public void afterEach() throws SQLException {
		
    }
	
	@AfterAll
    public static void afterAll() throws SQLException {
		connection.close();
    }

	
	protected Response executeGet(RequestSpecification r, String path) {
		return r.get(baseURL + path);
	}
	
	protected Response executePost(RequestSpecification r, String path) {
		return r.post(baseURL + path);
	}
	
	protected Response executePatch(RequestSpecification r, String path) {
		return r.patch(baseURL + path);
	}
	
	protected Response executeDelete(RequestSpecification r, String path) {
		return r.delete(baseURL + path);
	}

	
	protected static String getDeploymentsPath() {
		return deploymentsPath;
	}
	
	void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
}