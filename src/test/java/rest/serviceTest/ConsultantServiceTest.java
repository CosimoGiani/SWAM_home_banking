package rest.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BankAccount;
import model.Card;
import model.Consultant;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import model.enumeration.TransactionType;

public class ConsultantServiceTest extends ServiceTest {
	
	private User user;
	private Consultant consultant;
	private String OTP;
	private BankAccount account1;
	private BankAccount account2;
	private Transaction t1;
	private Transaction t2;
	private Card card;
	
	@Override
	protected void beforeEachInit() throws SQLException {
		
		setBaseURL("/home.banking/api/consultant/");
		
		QueryUtils.queryTruncateAll(connection);
		
		consultant = QueryUtils.queryCreateConsultant(connection, "123456", "Mario", "Rossi", "password");
		user = QueryUtils.queryCreateUser(connection, "prova@prova.it", "prova");
		account1 = QueryUtils.queryCreateBankAccount(connection, "1234", (float) 230, "000011110000IT", BankAccountType.ORDINARIO);
		account2 = QueryUtils.queryCreateBankAccount(connection, "0011", (float) 0, "222233332222IT", BankAccountType.INVESTITORE);
		t1 = QueryUtils.queryCreateTransaction(connection, (float) 27.4, LocalDate.now().minusMonths(3), "COOP", TransactionType.PAGAMENTO);
		t2 = QueryUtils.queryCreateTransaction(connection, (float) 57, LocalDate.now().minusMonths(2), "COOP", TransactionType.PAGAMENTO);
		card = QueryUtils.queryCreateCard(connection, "5555777711112222", CardType.RICARICABILE, LocalDate.now().plusYears(3).plusMonths(2), true, (float) 850);
		QueryUtils.queryLinkCardToAccount(connection, account1, card);
		QueryUtils.queryLinkUserToConsultant(connection, consultant, user);
		QueryUtils.queryLinkAccountToUser(connection, user, account1);
		QueryUtils.queryLinkAccountToUser(connection, user, account2);
		QueryUtils.queryLinkTransactionToAccount(connection, account1, t1);
		QueryUtils.queryLinkTransactionToAccount(connection, account1, t2);
	}
	
	@Test
	public void testGetAssociatedUsers() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		response = executeGet(request, "users");
				
		response.then().statusCode(200).contentType("application/json");
		
		body = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonArray arr = parser.parse(body).getAsJsonArray();
		JsonObject obj = arr.get(0).getAsJsonObject();
		
		assertEquals(obj.get("uuid").getAsString(), user.getUuid());
		assertEquals(obj.get("email").getAsString(), user.getEmail());
	}
	
	@Test
	public void testGetAssociatedUsersWhenNoUserIsAssociated() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		response = executeGet(request, "users");
				
		response.then().statusCode(204);
		
		body = response.getBody().asString();
		assertTrue(body.isEmpty());
	}
	
	@Test
	public void testGetUserDetails() {

		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1'}");
		response = executeGet(request, "user/details");
				
		response.then().statusCode(200).contentType("application/json");
		
		body = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(body).getAsJsonObject();
		
		assertEquals(obj.get("uuid").getAsString(), user.getUuid());
		assertEquals(obj.get("email").getAsString(), user.getEmail());
		
	}
	
	@Test
	public void testGetUserDetailsWhenNoUserIsAssociated() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1'}");
		response = executeGet(request, "user/details");
				
		response.then().statusCode(204);
		
		body = response.getBody().asString();
		assertTrue(body.isEmpty());
		
	}
	
	@Test
	public void testGetUserBankAccounts() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1'}");
		response = executeGet(request, "user/accounts");
				
		response.then().statusCode(200).contentType("application/json");
		
		body = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonArray arr = parser.parse(body).getAsJsonArray();
		
		assertTrue(arr.size() == 2);
		
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
	public void testUserGetBankAccountsWhenUserIsNotAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1'}");
		response = executeGet(request, "user/accounts");
				
		response.then().statusCode(204);
		
		body = response.getBody().asString();
		assertTrue(body.isEmpty());
	}
	
	@Test
	public void testModifyUserBankAccountType() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'accountType': 'INVESTITORE'}");
		response = executePatch(request, "user/modify-account-type");
				
		response.then().statusCode(200);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Il tipo del conto corrente è stato cambiato con successo", body);
		
	}
	
	@Test
	public void testModifyUserBankAccountTypeWhenTypeIsInvalid() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'accountType': 'INVALIDO'}");
		response = executePatch(request, "user/modify-account-type");
				
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Impossibile modificare il tipo del conto: tipo non valido", body);
		
	}
	
	@Test
	public void testModifyUserBankAccountTypeWhenUserIsNotAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'accountType': 'INVESTITORE'}");
		response = executePatch(request, "user/modify-account-type");
				
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Consulente non è referente dell'utente in questione", body);
		
	}
	
	@Test
	public void testModifyUserBankAccountTypeWhenTypeIsSame() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'accountType': 'ORDINARIO'}");
		response = executePatch(request, "user/modify-account-type");
				
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Il tipo del conto è già ORDINARIO", body);
		
	}
	
	@Test
	public void testModifyUserBankAccountTypeWhenAccountDoesNotExist() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3', 'accountType': 'INVESTITORE'}");
		response = executePatch(request, "user/modify-account-type");
				
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Impossibile modificare il tipo del conto", body);
		
	}
	
	@Test
	public void testGetBankAccountTransactions() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1'}");
		response = executeGet(request, "user/account/transactions");
		
		response.then().statusCode(200).contentType("application/json");

		body = response.getBody().asString();
		JsonParser parser = new JsonParser();
		JsonArray arr = parser.parse(body).getAsJsonArray();
		
		assertTrue(arr.size() == 2);
		
		JsonObject obj1 = arr.get(0).getAsJsonObject();
		JsonObject obj2 = arr.get(1).getAsJsonObject();
		
		Assertions.assertEquals(t1.getUuid(), obj1.get("uuid").getAsString());
		Assertions.assertEquals(t1.getAmount(), obj1.get("amount").getAsFloat());
		Assertions.assertEquals(t1.getDate().toString(), obj1.get("date").getAsString());
		Assertions.assertEquals(t1.getLocation(), obj1.get("location").getAsString());
		Assertions.assertEquals(t1.getTransactionType().toString(), obj1.get("transactionType").getAsString());
		
		Assertions.assertEquals(t2.getUuid(), obj2.get("uuid").getAsString());
		Assertions.assertEquals(t2.getAmount(), obj2.get("amount").getAsFloat());
		Assertions.assertEquals(t2.getDate().toString(), obj2.get("date").getAsString());
		Assertions.assertEquals(t2.getLocation(), obj2.get("location").getAsString());
		Assertions.assertEquals(t2.getTransactionType().toString(), obj2.get("transactionType").getAsString());
		
	}
	
	@Test
	public void testGetBankAccountTransactionsWhenUserIsNotAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1'}");
		response = executeGet(request, "user/account/transactions");
		
		response.then().statusCode(204);
		
		body = response.getBody().asString();
		assertTrue(body.isEmpty());
		
	}
	
	@Test
	public void testGetBankAccountTransacationsWhenBankAccountIsNotOwendByUser() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3'}");
		response = executeGet(request, "user/account/transactions");
		
		response.then().statusCode(204);
		
		body = response.getBody().asString();
		assertTrue(body.isEmpty());
		
	}
	
	@Test
	public void testGetBankAccountBalance() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1'}");
		response = executeGet(request, "user/account/balance");
		
		response.then().statusCode(200).contentType("application/json");

		body = response.getBody().asString();
		
		assertEquals(body, String.valueOf(account1.getBalance()));
		
	}
	
	@Test
	public void testGetBankAccountBalanceWhenNoUserIsAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1'}");
		response = executeGet(request, "user/account/balance");
		
		response.then().statusCode(204);

		body = response.getBody().asString();
		
		assertTrue(body.isEmpty());
		
	}
	
	@Test
	public void testGetBankAccountBalanceWhenAccountIsNotOwendByUser() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3'}");
		response = executeGet(request, "user/account/balance");
		
		response.then().statusCode(204);

		body = response.getBody().asString();
		
		assertTrue(body.isEmpty());
		
	}
	
	@Test
	public void testAddUserCard() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardNumber': '5222000011110000', 'massimale': '1000', 'cardType': 'CREDITO'}");
		response = executePost(request, "user/cards/add-card");
		
		response.then().statusCode(200);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta aggiunta con successo", body);
		
	}
	
	@Test
	public void testAddUserCardWhenCardTypeIsInvalid() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardNumber': '5222000011110000', 'massimale': '1000', 'cardType': 'INVALIDO'}");
		response = executePost(request, "user/cards/add-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non aggiunta: la tipologia della carta non è corretta", body);
		
	}
	
	@Test
	public void testAddUserCardWhenUserIsNotAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardNumber': '5222000011110000', 'massimale': '1000', 'cardType': 'CREDITO'}");
		response = executePost(request, "user/cards/add-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non aggiunta", body);
		
	}
	
	@Test
	public void testAddUserCardWhenBankAccountDoesNotExist() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3', 'cardNumber': '5222000011110000', 'massimale': '1000', 'cardType': 'CREDITO'}");
		response = executePost(request, "user/cards/add-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non aggiunta", body);
		
	}

	@Test
	public void testAddUserCardWhenCardDataIsInvalidOrMissing() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3', 'cardNumber': '5222000011110000', 'cardType': 'CREDITO'}");
		response = executePost(request, "user/cards/add-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non aggiunta", body);
		
	}
	
	@Test
	public void testRemoveUserCard() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '1'}");
		response = executeDelete(request, "user/cards/remove-card");
		
		response.then().statusCode(200);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta rimossa con successo", body);
		
	}
	
	@Test
	public void testRemoveUserCardWhenUserIsNotAssociatedToConsultant() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '1'}");
		response = executeDelete(request, "user/cards/remove-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non rimossa", body);
		
	}
	
	@Test
	public void testRemoveUserCardWhenBankAccountIsNotOwendByUser() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3', 'cardId': '1'}");
		response = executeDelete(request, "user/cards/remove-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non rimossa", body);
		
	}
	
	@Test
	public void testRemoveUserCardWhenCardDoesNotExist() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '2'}");
		response = executeDelete(request, "user/cards/remove-card");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		Assertions.assertEquals("Carta non rimossa: carta non appartente all'utente o non esistente", body);
		
	}
	
	@Test
	public void testUpdateMassimaleUserCard() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '1', 'massimale': '1000'}");
		response = executePatch(request, "user/cards/update-card-massimale");
		
		response.then().statusCode(200);
		
		body = response.getBody().asString();
		assertEquals("Il massimale della carta è stato cambiato con successo", body);
		
	}
	
	@Test
	public void testUpdateMassimaleUserCardWhenNoUserIsAssociated() throws SQLException {
		
		Consultant testConsultant = QueryUtils.queryCreateConsultant(connection, "654321", "Mario", "Verdi", "password2");
		OTP = getToken(testConsultant, "password2");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", testConsultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '1', 'massimale': '1000'}");
		response = executePatch(request, "user/cards/update-card-massimale");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		assertEquals("Non è stato possibile cambiare il massimale", body);
		
	}
	
	@Test
	public void testUpdateMassimaleUserCardWhenUserDoesNotOwnBankAccount() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '3', 'cardId': '1', 'massimale': '1000'}");
		response = executePatch(request, "user/cards/update-card-massimale");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		assertEquals("Non è stato possibile cambiare il massimale", body);
		
	}
	
	@Test
	public void testUpdateMassimaleUserCardWhenRequestIsInvalid() {
		
		OTP = getToken(consultant, "password");
		Assertions.assertNotEquals("", OTP);
		System.out.println("OTP generata: " + OTP);
		
		Response response;
		String body;
		
		RequestSpecification request = RestAssured.given();
		request.header("Authorization", consultant.getIdentificationNumber() + " " + OTP);
		request.header("Content-Type", "application/json");
		request.body("{'userId': '1', 'accountId': '1', 'cardId': '1', 'massimale_Invalid': '1000'}");	// dovrebbe essere solo "massimale"
		response = executePatch(request, "user/cards/update-card-massimale");
		
		response.then().statusCode(406);
		
		body = response.getBody().asString();
		assertEquals("Non è stato possibile cambiare il massimale", body);
		
	}
	
	// METODO PER RECUPERARE IL TOKEN DEL CONSULTANT	
	private String getToken(Consultant consultant, String decryptedPassword) {
		
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body("{'identificationNumber': '" + consultant.getIdentificationNumber() + "', 'password': '"+decryptedPassword+"'}");
		
		Response response = request.post("/home.banking/api/consultant/" + "login");
		response.then().statusCode(200).contentType("application/json");
		String body = response.getBody().asString();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonBody = parser.parse(body).getAsJsonObject();
		
		String token = jsonBody.get("token").getAsString();
		return token;
		
	}

}
