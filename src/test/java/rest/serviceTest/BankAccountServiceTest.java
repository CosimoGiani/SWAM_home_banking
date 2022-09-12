package rest.serviceTest;

import java.sql.SQLException;
import java.time.LocalDate;

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
		response.then().statusCode(200);
		body = response.getBody().asString();
		System.out.println(body);
	}
	

}
