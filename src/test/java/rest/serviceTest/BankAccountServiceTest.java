package rest.serviceTest;

import java.sql.SQLException;
import java.time.LocalDate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BankAccount;
import model.Card;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import model.enumeration.TransactionType;

public class BankAccountServiceTest extends ServiceTest {
	
	private User user;
	private String decryptedPassword;
	
	private BankAccount account;
	private Card card1;
	private Card card2;
	private Transaction t1;
	private Transaction t2;
	private Transaction t3;

	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/account/");
		
		QueryUtils.queryTruncateAll(connection);
		
		decryptedPassword = "prova";
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", decryptedPassword);
		account = QueryUtils.queryCreateBankAccount(connection, "1234", (float) 230, "000011110000IT", BankAccountType.ORDINARIO);
		QueryUtils.queryLinkAccountToUser(connection, user, account);
		
		card1 = QueryUtils.queryCreateCard(connection, "5222000011110000", CardType.DEBITO, LocalDate.now().plusYears(2), true,(float) 1000);
		QueryUtils.queryLinkCardToAccount(connection, account, card1);
		
		card2 = QueryUtils.queryCreateCard(connection, "5555777711112222", CardType.RICARICABILE, LocalDate.now().plusYears(3).plusMonths(2), true,(float) 850);
		QueryUtils.queryLinkCardToAccount(connection, account, card2);
		
		t1 = QueryUtils.queryCreateTransaction(connection, (float) 27.4, LocalDate.now().minusMonths(3), "COOP", TransactionType.PAGAMENTO);
		t2 = QueryUtils.queryCreateTransaction(connection, (float) 57, LocalDate.now().minusMonths(2), "COOP", TransactionType.PAGAMENTO);
		t3 = QueryUtils.queryCreateTransaction(connection, (float) 200, LocalDate.now().minusMonths(1), "ATM-7", TransactionType.VERSAMENTO);
		QueryUtils.queryLinkTransactionToAccount(connection, account, t1);
		QueryUtils.queryLinkTransactionToAccount(connection, account, t2);
		QueryUtils.queryLinkTransactionToAccount(connection, account, t3);
		
		System.out.println("Mock inizializzati con successo");
	}
	
	@Test
	public void testGetBankAccountTransactions() {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}");
		
		response = executeGet(request, "transactions");
		response.then().statusCode(200).contentType("application/json");
		body = response.getBody().asString();
		// System.out.println(body);
		
		JsonParser parser = new JsonParser();
		JsonArray jsonArray = parser.parse(body).getAsJsonArray();
		
		JsonObject retrievedT1 = jsonArray.get(0).getAsJsonObject();
		Assertions.assertEquals(t1.getUuid(), retrievedT1.get("uuid").getAsString());
		Assertions.assertEquals(t1.getAmount(), retrievedT1.get("amount").getAsFloat());
		Assertions.assertEquals(t1.getDate().toString(), retrievedT1.get("date").getAsString());
		Assertions.assertEquals(t1.getLocation(), retrievedT1.get("location").getAsString());
		Assertions.assertEquals(t1.getTransactionType().toString(), retrievedT1.get("transactionType").getAsString());
		
		JsonObject retrievedT2 = jsonArray.get(1).getAsJsonObject();
		Assertions.assertEquals(t2.getUuid(), retrievedT2.get("uuid").getAsString());
		Assertions.assertEquals(t2.getAmount(), retrievedT2.get("amount").getAsFloat());
		Assertions.assertEquals(t2.getDate().toString(), retrievedT2.get("date").getAsString());
		Assertions.assertEquals(t2.getLocation(), retrievedT2.get("location").getAsString());
		Assertions.assertEquals(t2.getTransactionType().toString(), retrievedT2.get("transactionType").getAsString());
		
		JsonObject retrievedT3 = jsonArray.get(2).getAsJsonObject();
		Assertions.assertEquals(t3.getUuid(), retrievedT3.get("uuid").getAsString());
		Assertions.assertEquals(t3.getAmount(), retrievedT3.get("amount").getAsFloat());
		Assertions.assertEquals(t3.getDate().toString(), retrievedT3.get("date").getAsString());
		Assertions.assertEquals(t3.getLocation(), retrievedT3.get("location").getAsString());
		Assertions.assertEquals(t3.getTransactionType().toString(), retrievedT3.get("transactionType").getAsString());
	}
	
	@Test
	public void testGetBankAccountTransactionsEmpty() throws SQLException {
		// Caso in cui non ci sono transazioni associate al BankAccount selezionato
		
		BankAccount account2 = QueryUtils.queryCreateBankAccount(connection, "0011", (float) 0, "222233332222IT", BankAccountType.INVESTITORE);
		QueryUtils.queryLinkAccountToUser(connection, user, account2);
		
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '2'}");
		
		response = executeGet(request, "transactions");
		response.then().statusCode(204); // status 204 (il server ha processato con successo la richiesta e non restituirà nessun contenuto)
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
	}
	
	@Test
	public void testGetBankAccountTransactionsOtherUser() throws SQLException {
		// Caso in cui un utente cerca di accedere alle transazioni di un conto che non possiede
		
		String decryptedPassword2 = "prova2";
		User user2 = QueryUtils.queryCreateUser(connection, "prova2@prova2.it", decryptedPassword2);
		
		String OTP2 = OTPUtils.getOtp(user2, decryptedPassword2);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user2.getEmail() + " " + OTP2); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}");
		
		response = executeGet(request, "transactions");
		response.then().statusCode(204); // un 403 sarebbe stato meglio...
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
	}
	
	
	@Test
	public void testGetBankAccountCards() {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}");
		
		response = executeGet(request, "cards");
		response.then().statusCode(200).contentType("application/json");
		body = response.getBody().asString();
		// System.out.println(body);
		
		JsonParser parser = new JsonParser();
		JsonArray jsonArray = parser.parse(body).getAsJsonArray();
		
		JsonObject retrievedCard1 = jsonArray.get(0).getAsJsonObject();
		Assertions.assertEquals(card1.getUuid(), retrievedCard1.get("uuid").getAsString());
		Assertions.assertEquals(card1.getCardNumber(), retrievedCard1.get("cardNumber").getAsString());
		Assertions.assertEquals(card1.getExpirationDate().toString(), retrievedCard1.get("expirationDate").getAsString());
		Assertions.assertEquals(card1.getMassimale(), retrievedCard1.get("massimale").getAsFloat());
		Assertions.assertEquals(card1.getCardType().toString(), retrievedCard1.get("cardType").getAsString());
		Assertions.assertEquals(true, retrievedCard1.get("active").getAsBoolean());
		
		JsonObject retrievedCard2 = jsonArray.get(1).getAsJsonObject();
		Assertions.assertEquals(card2.getUuid(), retrievedCard2.get("uuid").getAsString());
		Assertions.assertEquals(card2.getCardNumber(), retrievedCard2.get("cardNumber").getAsString());
		Assertions.assertEquals(card2.getExpirationDate().toString(), retrievedCard2.get("expirationDate").getAsString());
		Assertions.assertEquals(card2.getMassimale(), retrievedCard2.get("massimale").getAsFloat());
		Assertions.assertEquals(card2.getCardType().toString(), retrievedCard2.get("cardType").getAsString());
		Assertions.assertEquals(true, retrievedCard2.get("active").getAsBoolean());
		
	}
	
	@Test
	public void testGetBankAccountCardsEmpty() throws SQLException {
		// Caso in cui non ci sono Carte associate al BankAccount selezionato
		
		BankAccount account2 = QueryUtils.queryCreateBankAccount(connection, "0011", (float) 0, "222233332222IT", BankAccountType.INVESTITORE);
		QueryUtils.queryLinkAccountToUser(connection, user, account2);
		
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '2'}");
		
		response = executeGet(request, "cards");
		response.then().statusCode(204); // status 204 (il server ha processato con successo la richiesta e non restituirà nessun contenuto)
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
	}
	
	@Test
	public void testGetBankAccountCardsOtherUser() throws SQLException {
		// Caso in cui un utente cerca di accedere alle carte di un conto che non possiede
		
		String decryptedPassword2 = "prova2";
		User user2 = QueryUtils.queryCreateUser(connection, "prova2@prova2.it", decryptedPassword2);
		
		String OTP2 = OTPUtils.getOtp(user2, decryptedPassword2);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user2.getEmail() + " " + OTP2); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}");
		
		response = executeGet(request, "cards");
		response.then().statusCode(204); // un 403 sarebbe stato meglio...
		body = response.getBody().asString();
		Assertions.assertEquals("", body);
	}
	
	@Test
	public void testDeleteBankAccount() {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}");
		
		response = executeDelete(request, "delete-account");
		response.then().statusCode(200);
		body = response.getBody().asString();
		
		Assertions.assertEquals("Conto corrente chiuso con successo", body);
	}
	
	@Test
	public void testDeleteBankAccountNotPossible() throws SQLException {
		String OTP = OTPUtils.getOtp(user, decryptedPassword);
		
		RequestSpecification request;
		Response response;
		String body;
		
		// BankAccount non esistente
		request = RestAssured.given();
		request.header("Authorization", user.getEmail() + " " + OTP); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '2'}"); // nessun bank account ha questo id, perciò mi aspetto di non cancellare nulla
		
		response = executeDelete(request, "delete-account");
		response.then().statusCode(403);
		body = response.getBody().asString();
		
		Assertions.assertEquals("Impossibile chiudere il conto", body);
		
		// BankAccount di proprietà di un altro utente
		String decryptedPassword2 = "prova2";
		User user2 = QueryUtils.queryCreateUser(connection, "prova2@prova2.it", decryptedPassword2);
		String OTP2 = OTPUtils.getOtp(user2, decryptedPassword2);
		
		request = RestAssured.given();
		request.header("Authorization", user2.getEmail() + " " + OTP2); 
		request.header("Content-Type", "application/json");
		request.body("{'id': '1'}"); // Questo bankAccount è di proprietà di user, non di user2, perciò la cancellazione non viene eseguita
		
		response = executeDelete(request, "delete-account");
		response.then().statusCode(403); 
		body = response.getBody().asString(); 
		
		Assertions.assertEquals("Impossibile chiudere il conto", body); // In entrambi i casi il messaggio di errore è il medesimo
	}

}
