package controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Consultant;
import model.User;
import rest.controller.UserController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;

public class UserControllerTest {
	
	private UserController userController;
	private UserDao userDao;
	private User user;
	private BankAccount account1;
	private ConsultantDao consultantDao;
	private Consultant consultant;
	
	@BeforeEach
	public void setup() throws IllegalAccessException {
		userController = new UserController();
		userDao = mock(UserDao.class);
		consultantDao = mock(ConsultantDao.class);
		consultant = new Consultant(UUID.randomUUID().toString());
		consultant.setIdentificationNumber("123456");
		consultant.setEncryptedPassword("password");
		user = new User(UUID.randomUUID().toString());
		user.setEmail("user1@example.com");
        user.setEncryptedPassword("pass1");
        consultant.addUser(user);
        user.setConsultant(consultant);
		account1 = new BankAccount(UUID.randomUUID().toString());
		FieldUtils.writeField(userController, "userDao", userDao, true);
		FieldUtils.writeField(userController, "consultantDao", consultantDao, true);
	}
	
	@Test
	public void testGetAssociatedBankAccounts() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(account1);
		user.setBankAccounts(accounts);
		when(userDao.getUserIdFromEmail(user.getEmail())).thenReturn(1L);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		List<BankAccount> testAccounts = userController.getAssociatedBankAccounts(user.getEmail());
		assertEquals(1, testAccounts.size());
		assertEquals(account1, testAccounts.get(0));
	}
	
	@Test
	public void testGetUserFromEmail() {
		when(userDao.getUserFromEmail(user.getEmail())).thenReturn(user);
		User testUser = userController.getUserFromEmail(user.getEmail(), false);
		assertEquals(user.getId(), testUser.getId());  // sono entrambi null ma messo per completezza
		assertEquals(user.getUuid(), testUser.getUuid());
		assertEquals(user.getEmail(), testUser.getEmail());
		assertEquals(user.getPassword(), testUser.getPassword());
	}
	
	@Test
	public void testGetConsultantAssociated() {
		when(userDao.getConsultantIdFromEmail(user.getEmail())).thenReturn(12L);
		when(consultantDao.getConsultantLazy(12L, true)).thenReturn(consultant);
		Consultant testConsultant = userController.getConsultantAssociated(user.getEmail());
		assertEquals(user.getConsultant(), testConsultant);
		assertEquals(testConsultant.getUsers().get(0), user);
	}
	
	@Test
	public void testSendMessageToConsultant() {
		when(userDao.getUserFromEmail(user.getEmail())).thenReturn(user);
		when(userDao.getConsultantIdFromEmail(user.getEmail())).thenReturn(12L);
		when(consultantDao.getConsultantLazy(12L, true)).thenReturn(consultant);
		String msg = userController.sendMessageToConsultant(user.getEmail(), "oggetto", "testo");
		assertEquals(msg, "Messaggio inviato correttamente");
	}
	
}
