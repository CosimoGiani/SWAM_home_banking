package DaoTest;

import dao.UserDao;
import model.BankAccount;
import model.Consultant;
import model.User;
import model.enumeration.BankAccountType;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UserDaoTest extends JPATest {
	
	private UserDao userDao;
	private User user;
	private Consultant consultant;
	
	@Override
	protected void init() throws IllegalAccessException {
		System.out.println("Avvio init custom per UserDaoTest");
		
		consultant = new Consultant(UUID.randomUUID().toString());
        consultant.setIdentificationNumber("123456");
		consultant.setEncryptedPassword("password");
		entityManager.persist(consultant);
		
		user = new User(UUID.randomUUID().toString());
        user.setEmail("user1@example.com");
        user.setEncryptedPassword("pass1");
        user.setConsultant(consultant);
        
        BankAccount account1 = new BankAccount(UUID.randomUUID().toString());
		account1.setType(BankAccountType.ORDINARIO);
		account1.setBalance(10);
		user.addBankAccountToList(account1);
		
		BankAccount account2 = new BankAccount(UUID.randomUUID().toString());
		account2.setType(BankAccountType.ORDINARIO);
		account2.setBalance(20);
		user.addBankAccountToList(account2);
		
		entityManager.persist(user);                                                //persisto manualmente, senza passare dal DAO
        userDao = new UserDao();                                                    //UserDao creato manualmente - niente CDI!
        FieldUtils.writeField(userDao, "em", entityManager, true);
	}
	
	@Test
    public void testGetUserFromEmail() {
        User result = userDao.getUserFromEmail(user.getEmail());
        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals(user.getUuid(), result.getUuid());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Assertions.assertEquals(user.getPassword(), result.getPassword());
    }
	
	@Test
    public void testSave() {                                                            // test di funzionalità di tipo SAVE
        User userToPersist = new User(UUID.randomUUID().toString());
        userToPersist.setEmail("user2@example.com");
        userToPersist.setEncryptedPassword("pass2");
        userDao.save(userToPersist);                                                  // questa volta persisto tramite DAO ..
        User manuallyRetrievedUser = entityManager.
                createQuery("FROM User WHERE uuid = :uuid", User.class)
                .setParameter("uuid", userToPersist.getUuid())           	            // ..ed estraggo manualmente tramite query in JPQL
                .getSingleResult();
        Assertions.assertEquals(userToPersist, manuallyRetrievedUser);    			// verifico poi l'uguaglianza tramite asserzioni
    }
	
	@Test
	public void testIsEmailInDB() {
		Assertions.assertTrue(userDao.isEmailInDB("user1@example.com"));
		Assertions.assertFalse(userDao.isEmailInDB("user2@example.com"));
	}
	
	@Test
	public void testCheckCredentials() {
		User userNotPersisted = new User(UUID.randomUUID().toString());
		userNotPersisted.setEmail("testUser@example.com");
		userNotPersisted.setEncryptedPassword("testPassword");
		Assertions.assertTrue(userDao.checkCredentials(user.getEmail(), user.getPassword()));
		Assertions.assertFalse(userDao.checkCredentials(userNotPersisted.getEmail(), userNotPersisted.getPassword()));
	}
	
	@Test
	public void testGetUserIdFromEmail() {
		Assertions.assertEquals(user.getId(), userDao.getUserIdFromEmail(user.getEmail()));
		Assertions.assertThrows(NoResultException.class, ()->{
			userDao.getUserIdFromEmail("testUser@example.com");
		});
	}
	
	@Test
	public void testGetConsultantIdFromEmail() {
		Assertions.assertEquals(user.getConsultant().getId(), userDao.getConsultantIdFromEmail(user.getEmail()));
		Assertions.assertThrows(NoResultException.class, ()->{
			userDao.getConsultantIdFromEmail("testUser@example.com");
		});
	}
	
	@Test
	public void testGetAssociatedBankAccounts() {
		List<BankAccount> accountsExpected = user.getBankAccounts();
		List<BankAccount> accountsActual = userDao.getAssociatedBankAccounts(user.getId());
		Assertions.assertEquals(accountsExpected, accountsActual);
		/*Assertions.assertThrows(NotFoundException.class, ()->{
			userDao.getAssociatedBankAccounts(user.getId());
		});*/
	}

}
