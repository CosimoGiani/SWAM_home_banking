package rest.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.BankAccountDao;
import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Consultant;
import model.Transaction;
import model.User;
import rest.controller.ConsultantController;

public class ConsultantControllerTest {
	
	private ConsultantController consultantController;
	private ConsultantDao consultantDao;
	private Consultant consultant;
	private Consultant mockedConsultant;
	private UserDao userDao;
	private User user;
	private BankAccountDao accountDao;
	private BankAccount account;
	private BankAccount mockedAccount;
	
	@BeforeEach
	public void setup() throws IllegalAccessException {
		consultantController = new ConsultantController();
		consultantDao = mock(ConsultantDao.class);
		userDao = mock(UserDao.class);
		accountDao = mock(BankAccountDao.class);
		consultant = new Consultant(UUID.randomUUID().toString());
		consultant.setIdentificationNumber("123456");
		consultant.setEncryptedPassword("password");
		user = new User(UUID.randomUUID().toString());
		user.setEmail("prova1@example.com");
		user.setEncryptedPassword("password");
		//user.setConsultant(consultant);
		account = new BankAccount(UUID.randomUUID().toString());
		mockedAccount = mock(BankAccount.class);
		mockedConsultant = mock(Consultant.class);
		FieldUtils.writeField(consultantController, "consultantDao", consultantDao, true);
		FieldUtils.writeField(consultantController, "userDao", userDao, true);
		FieldUtils.writeField(consultantController, "accountDao", accountDao, true);
	}
	
	@Test
	public void testGetAssociatedUsers() throws Exception {
		List<User> usersAssociated = new ArrayList<User>();
		usersAssociated.add(user);
		consultant.setUsers(usersAssociated);
		when(consultantDao.getConsultantIdFromIdNumber(consultant.getIdentificationNumber())).thenReturn(12L);
		when(consultantDao.getAssociatedUsers(12L)).thenReturn(consultant.getUsers());
		List<User> testUsers = consultantController.getAssociatedUsers(consultant.getIdentificationNumber(), false);
		assertEquals(1, testUsers.size());
		assertEquals(user, testUsers.get(0));
	}
	
	@Test
	public void testGetAssociatedUsersWhenExceptionIsThrown() {
		List<User> usersAssociated = new ArrayList<User>();
		consultant.setUsers(usersAssociated);
		when(consultantDao.getConsultantIdFromIdNumber(consultant.getIdentificationNumber())).thenReturn(12L);
		when(consultantDao.getAssociatedUsers(12L)).thenReturn(consultant.getUsers());
		assertThrows(Exception.class, ()->{
			consultantController.getAssociatedUsers(consultant.getIdentificationNumber(), false);
		});
	}
	
	@Test
	public void testGetConsultantIdFromIdNumber() throws Exception {
		when(consultantDao.getConsultantIdFromIdNumber(consultant.getIdentificationNumber())).thenReturn(12L);
		assertEquals(12L, consultantController.getConsultantIdFromIdNumber(consultant.getIdentificationNumber()));
	}
	
	@Test
	public void testGetConsultantIdFromIdNumberWhenExceptionIsThrown() {
		when(consultantDao.getConsultantIdFromIdNumber(consultant.getIdentificationNumber())).thenReturn(null);
		assertThrows(Exception.class, ()->{
			consultantController.getConsultantIdFromIdNumber(consultant.getIdentificationNumber());
		});
	}
	
	@Test
	public void testGetUserDetails() throws Exception {
		when(userDao.getUserFromId(1L)).thenReturn(user);
		User testUser = consultantController.getUserDetails(1L, false);
		assertEquals(user, testUser);
	}
	
	@Test
	public void testGetUserDetailsWhenExceptionIsThrown() {
		when(userDao.getUserFromId(1L)).thenReturn(null);
		assertThrows(Exception.class, ()->{
			consultantController.getUserDetails(1L, false);
		});
	}
	
	@Test
	public void testCheckUserIsAssociated() {
		user.setConsultant(mockedConsultant);
		when(userDao.getUserFromId(1L)).thenReturn(user);
		when(mockedConsultant.getId()).thenReturn(12L);
		assertTrue(consultantController.checkUserIsAssociated(12L, 1L));
		assertFalse(consultantController.checkUserIsAssociated(consultant.getId(), 1L));
	}
	
	@Test
	public void testGetUserBankAccounts() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(account);
		user.setBankAccounts(accounts);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		List<BankAccount> testAccounts = consultantController.getUserBankAccounts(1L);
		assertEquals(1, testAccounts.size());
		assertEquals(account, testAccounts.get(0));
	}
	
	@Test
	public void testGetBankAccountOwnedByUser() throws Exception {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(mockedAccount);
		user.setBankAccounts(accounts);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(123L);
		BankAccount testAccount = consultantController.getBankAccountOwnedByUser(123L, 1L);
		assertEquals(mockedAccount, testAccount);		
	}
	
	@Test
	public void testGetBankAccountOwnedByUserWhenExceptionIsThrown() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		user.setBankAccounts(accounts);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(123L);
		assertThrows(Exception.class, ()->{
			consultantController.getBankAccountOwnedByUser(123L, 1L);
		});
	}
	
	@Test
	public void testGetBankAccountTransactions() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t = new Transaction(UUID.randomUUID().toString());
		transactions.add(t);
		account.setTransactions(transactions);
		when(accountDao.getBankAccountTransactions(1L)).thenReturn(account.getTransactions());
		List<Transaction> testTransactions = consultantController.getBankAccountTransactions(1L);
		assertEquals(1, testTransactions.size());
		assertEquals(t, testTransactions.get(0));
	}
	
	@Test
	public void testGetBankAccountLazy() throws Exception {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(mockedAccount);
		user.setBankAccounts(accounts);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(123L);
		when(accountDao.getAccountById(123L)).thenReturn(mockedAccount);
		BankAccount testAccount = consultantController.getBankAccountLazy(123L, 1L);
		assertEquals(mockedAccount, testAccount);
	}
	
	@Test
	public void testGetBankAccountLazyWhenExceptionIsThrown() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		user.setBankAccounts(accounts);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(123L);
		when(accountDao.getAccountById(123L)).thenReturn(mockedAccount);
		assertThrows(Exception.class, ()->{
			consultantController.getBankAccountLazy(123L, 1L);
		});
	}

}
