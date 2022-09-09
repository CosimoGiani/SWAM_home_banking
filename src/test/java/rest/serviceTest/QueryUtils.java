package rest.serviceTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import model.BankAccount;
import model.Card;
import model.Consultant;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;

public class QueryUtils {
	
	public static String queryCreate(Class c) {
		
		/*if(c == User.class) {
			return "INSERT INTO users(uuid, email, firstname, lastname, password) "
					+ " VALUES (?, ?, ?, ?, ?)";
			
		} else*/ if (c == Card.class) {
			return "INSERT INTO cards(uuid, cardNumber, cardType, expirationDate, isActive, massimale) "
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			
		} else if (c == Transaction.class) {
			return "INSERT INTO transactions(uuid, amount, date, location, transactionType)"
					+ " VALUES (?, ?, ?, ?, ?)";
			
		} else if(c == Consultant.class) {
			return "INSERT INTO consultants(uuid, email, firstname, identificationNumber, lastname, password, phoneNumber)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
			
		} /*else if(c == BankAccount.class) {
			return "INSERT INTO bankAccounts(uuid, accountNumber, balance, iban, type)"
					+ " VALUES (?, ?, ?, ?, ?)";
			
		} */ else {
			return "";
		}
			
	}
	
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

}
