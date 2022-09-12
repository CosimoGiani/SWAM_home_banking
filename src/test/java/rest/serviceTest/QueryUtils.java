package rest.serviceTest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

import model.BankAccount;
import model.Card;
import model.Consultant;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import model.enumeration.TransactionType;

public class QueryUtils {
	
	
	/* TRUNCATE operation */
	public static void queryTruncateAll(Connection connection) throws SQLException {
		// Pulisce tutte le tuple nelle tabelle del DB
		Statement statement = connection.createStatement();
		
		String query1 = "SET FOREIGN_KEY_CHECKS = 0";
		String query2 = "TRUNCATE users";
		String query3 = "TRUNCATE bankAccounts";
		String query4 = "TRUNCATE cards";
		String query5 = "TRUNCATE transactions";
		String query6 = "TRUNCATE consultants";
		String query7 = "SET FOREIGN_KEY_CHECKS = 1";
		
		statement.addBatch(query1);
		statement.addBatch(query2);
		statement.addBatch(query3);
		statement.addBatch(query4);
		statement.addBatch(query5);
		statement.addBatch(query6);
		statement.addBatch(query7);
		
		statement.executeBatch();
		statement.close();
		
	}
	
	
	/* CREATE operations*/
	public static User queryCreateUser(Connection connection, String email, String password) throws SQLException {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		
		String query = "INSERT INTO users(uuid, email, password) VALUES (?, ?, ?)";
		
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, user.getUuid());
		statement.setString(2, user.getEmail());
		statement.setString(3, user.getPassword());
		
		statement.executeUpdate();
		statement.close();
		
		return user;
	}
	
	public static User queryCreateUser(Connection connection, String email, String password, String firstname, String lastname) throws SQLException {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		user.setFirstname(firstname);
		user.setLastname(lastname);
		
		String query = "INSERT INTO users(uuid, email, password, firstname, lastname) VALUES (?, ?, ?, ?, ?)";
		
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, user.getUuid());
		statement.setString(2, user.getEmail());
		statement.setString(3, user.getPassword());
		statement.setString(4, user.getFirstname());
		statement.setString(5, user.getLastname());
		
		statement.executeUpdate();
		statement.close();
			
		return user;
	}
	
	public static BankAccount queryCreateBankAccount(Connection connection, String accountNumber, float balance, String iban, BankAccountType type) throws SQLException {
		BankAccount account = new BankAccount(UUID.randomUUID().toString());
		account.setAccountNumber(accountNumber);
		account.setBalance(balance);
		account.setIban(iban);
		account.setType(type);
		
		String query = "INSERT INTO bankAccounts(uuid, accountNumber, balance, iban, type) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, account.getUuid());
		statement.setString(2, account.getAccountNumber());
		statement.setFloat(3, account.getBalance());
		statement.setString(4, account.getIban());
		
		if(account.getType() == BankAccountType.ORDINARIO)
			statement.setInt(5, 0);
		else if(account.getType() == BankAccountType.UNDER30)
			statement.setInt(5, 1);
		else if(account.getType() == BankAccountType.INVESTITORE)
			statement.setInt(5, 2);
		
		statement.executeUpdate();
		statement.close();
		
		return account;
	}	
	
	public static Card queryCreateCard(Connection connection, String cardNumber, CardType cardType, LocalDate expirationDate, boolean isActive, float massimale) throws SQLException {
		Card card = new Card(UUID.randomUUID().toString());
		card.setCardNumber(cardNumber);
		card.setCardType(cardType);
		card.setExpirationDate(expirationDate);
		card.setActive(isActive);
		card.setMassimale(massimale);
		
		String query = "INSERT INTO cards(uuid, cardNumber, cardType, expirationDate, isActive, massimale) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, card.getUuid());
		statement.setString(2, card.getCardNumber());
		
	
		statement.setDate(4, Date.valueOf(expirationDate));
		
		statement.setBoolean(5, isActive);
		statement.setFloat(6, card.getMassimale());
		
		if(card.getCardType() == CardType.CREDITO)
			statement.setInt(3, 0);
		else if(card.getCardType() == CardType.DEBITO)
			statement.setInt(3, 1);
		else if(card.getCardType() == CardType.RICARICABILE)
			statement.setInt(3, 2);
		
		statement.executeUpdate();
		statement.close();
		
		return card;
	}
	
	public static Transaction queryCreateTransaction(Connection connection, float amount, LocalDate date, String location, TransactionType transactionType) throws SQLException {
		Transaction transaction = new Transaction(UUID.randomUUID().toString());
		transaction.setAmount(amount);
		transaction.setDate(date);
		transaction.setLocation(location);
		transaction.setTransactionType(transactionType);
		
		String query = "INSERT INTO transactions(uuid, amount, date, location, transactionType) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, transaction.getUuid());
		statement.setFloat(2, transaction.getAmount());
		
		statement.setDate(3, Date.valueOf(date));
		
		statement.setString(4, transaction.getLocation());
		
		if(transaction.getTransactionType() == TransactionType.ADDEBITO)
			statement.setInt(5, 0);
		else if(transaction.getTransactionType() == TransactionType.BONIFICO)
			statement.setInt(5, 1);
		else if(transaction.getTransactionType() == TransactionType.PAGAMENTO)
			statement.setInt(5, 2);
		else if(transaction.getTransactionType() == TransactionType.VERSAMENTO)
			statement.setInt(5, 3);
		
		statement.executeUpdate();
		statement.close();
		
		return transaction;
	}
	
	public static Consultant queryCreateConsultant(Connection connection, String email, String firstname, String identificationNumber, String lastname, String password, String phoneNumber) throws SQLException {
		Consultant consultant = new Consultant(UUID.randomUUID().toString());
		consultant.setEmail(email);
		consultant.setFirstname(firstname);
		consultant.setIdentificationNumber(identificationNumber);
		consultant.setLastname(lastname);
		consultant.setEncryptedPassword(password);
		consultant.setPhoneNumber(phoneNumber);
		
		String query = "INSERT INTO consultants(uuid, email, firstname, identificationNumber, lastname, password, phoneNumber) VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, consultant.getUuid());
		statement.setString(2, consultant.getEmail());
		statement.setString(3, consultant.getFirstname());
		statement.setString(4, consultant.getIdentificationNumber());
		statement.setString(5, consultant.getLastname());
		statement.setString(6, consultant.getPassword());
		statement.setString(7, consultant.getPhoneNumber());
		
		statement.executeUpdate();
		statement.close();
		
		return consultant;		
	}
	
	public static Consultant queryCreateConsultant(Connection connection, String identificationNumber, String firstname, String lastname, String password) throws SQLException {
		Consultant consultant = new Consultant(UUID.randomUUID().toString());
		consultant.setFirstname(firstname);
		consultant.setIdentificationNumber(identificationNumber);
		consultant.setLastname(lastname);
		consultant.setEncryptedPassword(password);
		
		String query = "INSERT INTO consultants(uuid, identificationNumber, firstname, lastname, password) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, consultant.getUuid());
		statement.setString(2, consultant.getIdentificationNumber());
		statement.setString(3, consultant.getFirstname());
		statement.setString(4, consultant.getLastname());
		statement.setString(5, consultant.getPassword());
		
		statement.executeUpdate();
		statement.close();
		
		return consultant;		
	}
	
	
	/* LINK operations */
	public static void queryLinkAccountToUser(Connection connection, User user, BankAccount account) throws SQLException {
		
		Long userId = queryGetUserId(connection, user);
		
		Long accountId = queryGetBankAccountId(connection, account);
		
		String query = "UPDATE bankAccounts SET user_id=? WHERE id=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, userId);
		statement.setLong(2, accountId);
		
		statement.executeUpdate();
		statement.close();
		
		user.addBankAccountToList(account);
		
		System.out.println("All'utente "+userId+" è stato collegato l'account "+accountId);
	}
	
	public static void queryLinkCardToAccount(Connection connection, BankAccount account, Card card) throws SQLException {
		
		Long accountId = queryGetBankAccountId(connection, account);
		
		Long cardId = queryGetCardId(connection, card);
		
		String query = "UPDATE cards SET account_id=? WHERE id=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, accountId);
		statement.setLong(2, cardId);
		
		statement.executeUpdate();
		statement.close();
		
		account.addCard(card);
		
		System.out.println("All'account "+accountId+" è stata collegata la carta "+cardId);
	}

	public static void queryLinkTransactionToAccount(Connection connection, BankAccount account, Transaction transaction) throws SQLException {
		
		Long accountId = queryGetBankAccountId(connection, account);
		
		Long transactionId = queryGetTransactionId(connection, transaction);
		
		String query = "UPDATE transactions SET account_id=? WHERE id=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, accountId);
		statement.setLong(2, transactionId);
		
		statement.executeUpdate();
		statement.close();
		
		account.addTransaction(transaction);
		
		System.out.println("All'account "+accountId+" è stata collegata la transazione "+transactionId);
	}
	
	public static void queryLinkUserToConsultant(Connection connection, Consultant consultant, User user) throws SQLException {
		
		Long consultantId = queryGetConsultantId(connection, consultant);
		
		Long userId = queryGetUserId(connection, user);
		
		String query = "UPDATE users SET consultant_id=? WHERE id=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, consultantId);
		statement.setLong(2, userId);
		
		statement.executeUpdate();
		statement.close();
		
		consultant.addUser(user);
		user.setConsultant(consultant);
		
		System.out.println("Al consulente "+consultantId+" è stato collegato l'utente "+userId);
	}
	
	
	/* GET IDs operations */
	private static Long queryGetUserId(Connection connection, User user) throws SQLException {
		String query = "SELECT id FROM users WHERE uuid=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, user.getUuid());
		
		ResultSet resultSet = statement.executeQuery();
		statement.close();
		
		resultSet.next();
		return resultSet.getLong("id");
	}
	
	private static Long queryGetBankAccountId(Connection connection, BankAccount account) throws SQLException {
		String query = "SELECT id FROM bankAccounts WHERE uuid=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, account.getUuid());
		
		ResultSet resultSet = statement.executeQuery();
		
		statement.close();
		
		resultSet.next();
		return resultSet.getLong("id");		
	}
	
	private static Long queryGetCardId(Connection connection, Card card) throws SQLException {
		String query = "SELECT id FROM cards WHERE uuid=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, card.getUuid());
		
		ResultSet resultSet = statement.executeQuery();
		
		statement.close();
		
		resultSet.next();
		return resultSet.getLong("id");		
	}
	
	private static Long queryGetTransactionId(Connection connection, Transaction transaction) throws SQLException {
		String query = "SELECT id FROM transactions WHERE uuid=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, transaction.getUuid());
		
		ResultSet resultSet = statement.executeQuery();
		
		statement.close();
		
		resultSet.next();
		return resultSet.getLong("id");		
	}
	
	private static Long queryGetConsultantId(Connection connection, Consultant consultant) throws SQLException {
		String query = "SELECT id FROM consultants WHERE uuid=?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, consultant.getUuid());
		
		ResultSet resultSet = statement.executeQuery();
		
		statement.close();
		
		resultSet.next();
		return resultSet.getLong("id");		
	}

}
