package utils;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;

import dao.BankAccountDao;
import dao.CardDao;
import dao.TransactionDao;
import dao.UserDao;
import model.BankAccount;
import model.Card;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import model.enumeration.TransactionType;

@Singleton
@Startup
public class LoadMocks {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private BankAccountDao accountDao;
	
	@Inject
	private CardDao cardDao;
	
	@Inject
	private TransactionDao transactionDao;
	
	@PostConstruct
	@Transactional
	public void init(){
		
		System.out.println("initializing database.. ");
		User user1 = createUser("user1@example.com", "pass1");
		User user2 = createUser("user2@example.com", "pass2");
		
		BankAccount account1 = createBankAccount(user1);
		BankAccount account2 = createBankAccount(user1);
		BankAccount account3 = createBankAccount(user2);
		
		Card card1 = createCard(account1, "7777 5555 3333 1111", (float) 1500, CardType.DEBITO);
		Card card2 = createCard(account1, "4444 5555 3333 1111", (float) 3500, CardType.CREDITO);
		Card card3 = createCard(account2, "4444 0000 3333 1111", (float) 2000, CardType.DEBITO);
		Card card4 = createCard(account3, "0000 0000 3333 1111", (float) 500, CardType.RICARICABILE);
		
		Transaction t1 = createTransaction(account1, (float) 500, TransactionType.VERSAMENTO, LocalDate.now().minusMonths(2), "ATM 3");
		Transaction t2 = createTransaction(account1, (float) 300, TransactionType.PAGAMENTO, LocalDate.now().minusMonths(1), "Coop");
		Transaction t3 = createTransaction(account2, (float) 300, TransactionType.VERSAMENTO, LocalDate.now().minusMonths(1), "ATM 1");
		Transaction t4 = createTransaction(account2, (float) 70, TransactionType.BONIFICO, LocalDate.now().minusDays(15), "Home-Banking App");
		
		userDao.save(user1);
		userDao.save(user2);
		
		accountDao.save(account1);
		accountDao.save(account2);
		accountDao.save(account3);
		
		cardDao.save(card1);
		cardDao.save(card2);
		cardDao.save(card3);
		cardDao.save(card4);
		
		transactionDao.save(t1);
		transactionDao.save(t2);
		transactionDao.save(t3);
		transactionDao.save(t4);
		
		System.out.println(".. database initialized! ");

	}
	
	private User createUser(String email, String password) {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		return user;
	}
	
	private BankAccount createBankAccount(User user) {
		BankAccount account = new BankAccount(UUID.randomUUID().toString());
		user.addBankAccountToList(account);
		account.setType(BankAccountType.ORDINARIO);
		account.setBalance(0);
		return account;
	}
	
	private Card createCard(BankAccount account, String cardNumber, float massimale, CardType cardType) {
		Card card = new Card(UUID.randomUUID().toString());
		account.addCard(card);
		card.setCardNumber(cardNumber);
		card.setExpirationDate(LocalDate.now().plusYears(2));
		card.setMassimale(massimale);
		card.setCardType(cardType);
		return card;
	}
	
	private Transaction createTransaction(BankAccount account, float amount, TransactionType transactionType, LocalDate date, String location) {
		Transaction t = new Transaction(UUID.randomUUID().toString());
		account.addTransaction(t);
		t.setAmount(amount);
		t.setTransactionType(transactionType);
		t.setDate(date);
		t.setLocation(location);
		if(transactionType ==  TransactionType.VERSAMENTO) {
			account.setBalance(account.getBalance() + amount);
		} else {
			account.setBalance(account.getBalance() - amount);
		}
		return t;
	}

}
