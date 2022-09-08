package controllerTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.BankAccountDao;
import dao.UserDao;
import model.BankAccount;
import model.Card;
import model.Transaction;
import model.User;
import rest.controller.BankAccountController;

public class BankAccountControllerTest {
	
	private BankAccountController accountController;
	private BankAccount account;
	private BankAccount mockedAccount;
	private BankAccountDao bankAccountDao;
	private Transaction transaction;
	private Card card;
	private UserDao userDao;
	private User user;
	
	@BeforeEach
	public void setup() throws IllegalAccessException {
		accountController = new BankAccountController();
		account = new BankAccount(UUID.randomUUID().toString());
		transaction = new Transaction(UUID.randomUUID().toString());
		card = new Card(UUID.randomUUID().toString());
		user = new User(UUID.randomUUID().toString());
		user.setEmail("prova1@example.com");
		user.setEncryptedPassword("password");
		mockedAccount = mock(BankAccount.class);
		bankAccountDao = mock(BankAccountDao.class);
		userDao = mock(UserDao.class);
		FieldUtils.writeField(accountController, "bankAccountDao", bankAccountDao, true);
		FieldUtils.writeField(accountController, "userDao", userDao, true);
	}
	
	@Test
	public void testGetBankAccountTransactions() throws Exception {
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);
		account.setTransactions(transactions);
		when(bankAccountDao.getBankAccountTransactions(1L)).thenReturn(account.getTransactions());
		List<Transaction> testTransactions = accountController.getBankAccountTransactions(1L);
		assertEquals(1, testTransactions.size());
		assertEquals(transaction, testTransactions.get(0));
	}
	
	@Test
	public void testGetBankAccountTransactionsWhenExceptionIsThrown() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		account.setTransactions(transactions);
		when(bankAccountDao.getBankAccountTransactions(1L)).thenReturn(account.getTransactions());
		assertThrows(Exception.class, ()->{
			accountController.getBankAccountTransactions(1L);
		});
	}
	
	@Test
	public void testGetBankAccountCards() throws Exception {
		List<Card> cards = new ArrayList<Card>();
		cards.add(card);
		account.setCards(cards);
		when(bankAccountDao.getBankAccountCards(1L)).thenReturn(account.getCards());
		List<Card> testCards = accountController.getBankAccountCards(1L);
		assertEquals(1,  testCards.size());
		assertEquals(card,  testCards.get(0));
	}
	
	@Test
	public void testGetBankAccountCardsWhenExceptionIsThrown() {
		List<Card> cards = new ArrayList<Card>();
		account.setCards(cards);
		when(bankAccountDao.getBankAccountCards(1L)).thenReturn(account.getCards());
		assertThrows(Exception.class, ()->{
			accountController.getBankAccountCards(1L);
		});
	}
	
	@Test
	public void testUserOwnsBankAccount() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(mockedAccount);
		user.setBankAccounts(accounts);
		when(userDao.getUserIdFromEmail(user.getEmail())).thenReturn(1L);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(12L);
		assertTrue(accountController.userOwnsBankAccount(user.getEmail(), 12L));
		assertFalse(accountController.userOwnsBankAccount(user.getEmail(), account.getId()));
	}

}
