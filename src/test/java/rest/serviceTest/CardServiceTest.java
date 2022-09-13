package rest.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BankAccount;
import model.Card;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;

public class CardServiceTest extends ServiceTest {
	
	private User user;
	private String decryptedPassword;
	private BankAccount account;
	private Card card;
	private String OTP;
	
	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/card/");
		
		QueryUtils.queryTruncateAll(connection);
		
		decryptedPassword = "prova";
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", decryptedPassword);
		account = QueryUtils.queryCreateBankAccount(connection, "1234", (float) 230, "000011110000IT", BankAccountType.ORDINARIO);
		QueryUtils.queryLinkAccountToUser(connection, user, account);
		
		card = QueryUtils.queryCreateCard(connection, "5222000011110000", CardType.DEBITO, LocalDate.now().plusYears(2), true, (float) 1000);
		QueryUtils.queryLinkCardToAccount(connection, account, card);
		
	}
	
	@Test
	public void testBlockCard() {

		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'card_id': '1'}");
		response = executePatch(request, "block");
		response.then().statusCode(202);
		body = response.getBody().asString();
		
		assertEquals("Carta bloccata con successo", body);
	}
	
	@Test
	public void testBlockCardButUserHasNoCards() throws SQLException {
		
		User testUser = QueryUtils.queryCreateUser(connection, "prova2@prova2.it", "password");
		BankAccount testAccount = QueryUtils.queryCreateBankAccount(connection, "4321", (float) 150, "000011210000IT", BankAccountType.ORDINARIO);
		QueryUtils.queryLinkAccountToUser(connection, testUser, testAccount);
		Assertions.assertTrue(testAccount.getCards().isEmpty());
		
		OTP = OTPUtils.getOtp(testUser, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testUser.getEmail() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'card_id': '1'}");
		response = executePatch(request, "block");
		response.then().statusCode(403);
		body = response.getBody().asString();
		
		assertEquals("", body);
	}
	
	@Test
	public void testBlockCardJsonIsIncorrect() {

		OTP = OTPUtils.getOtp(user, decryptedPassword);
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'cards_identifications': '1'}");		// si ha 400 se il json della richiesta non è strutturato bene o le chiavi sono misspelled
		response = executePatch(request, "block");
		response.then().statusCode(400);
		body = response.getBody().asString();
		
		assertEquals("", body);
	}

}
