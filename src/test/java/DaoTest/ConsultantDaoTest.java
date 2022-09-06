package DaoTest;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dao.ConsultantDao;
import model.BankAccount;
import model.Consultant;
import model.User;
import model.enumeration.BankAccountType;

public class ConsultantDaoTest extends JPATest {
	
	private ConsultantDao consultantDao;
	private Consultant consultant;
	private User user;
	private BankAccount account;
	
	@Override
	protected void init() throws IllegalAccessException {
		System.out.println("Avvio init custom per ConsultantDaoTest");
		
		consultant = new Consultant(UUID.randomUUID().toString());
        consultant.setIdentificationNumber("123456");
		consultant.setEncryptedPassword("password");
		
		user = new User(UUID.randomUUID().toString());
		user.setEmail("prova1@example.com");
		user.setEncryptedPassword("pass1");
		user.setConsultant(consultant);
		
		account = new BankAccount(UUID.randomUUID().toString());
		account.setType(BankAccountType.ORDINARIO);
		account.setBalance(10);
		user.addBankAccountToList(account);
		
		consultant.addUser(user);
		
		entityManager.persist(consultant);
		entityManager.persist(user);
		entityManager.persist(account);
		consultantDao = new ConsultantDao();                                                   
        FieldUtils.writeField(consultantDao, "em", entityManager, true);
	}
	
	@Test
    public void testSave() {                                                            
        Consultant consultantToPersist = new Consultant(UUID.randomUUID().toString());
        consultantToPersist.setIdentificationNumber("000111");
        consultantToPersist.setEncryptedPassword("pass2");
        consultantDao.save(consultantToPersist);                                             
        Consultant manuallyRetrievedConsultant = entityManager.
                createQuery("FROM Consultant WHERE uuid = :uuid", Consultant.class)
                .setParameter("uuid", consultantToPersist.getUuid())           	           
                .getSingleResult();
        Assertions.assertEquals(consultantToPersist, manuallyRetrievedConsultant);
    }
	
	@Test
	public void testUpdate() {
		User userTest = new User(UUID.randomUUID().toString());
		userTest.setEmail("user1@example.com");
        userTest.setEncryptedPassword("pass1");
        consultant.addUser(userTest);
        consultantDao.update(consultant);
        Consultant manuallyRetrievedConsultant = entityManager.
        		createQuery("FROM Consultant WHERE uuid = :uuid", Consultant.class)
        		.setParameter("uuid", consultant.getUuid())
        		.getSingleResult();
        Assertions.assertTrue(manuallyRetrievedConsultant.getUsers().contains(userTest));
	}
	
	@Test
	public void testCheckCredentials() {
		Consultant consultantNotPersisted = new Consultant(UUID.randomUUID().toString());
		consultantNotPersisted.setIdentificationNumber("111222");
		consultantNotPersisted.setEncryptedPassword("testPassword");
		Assertions.assertTrue(consultantDao.checkCredentials(consultant.getIdentificationNumber(), consultant.getPassword()));
		Assertions.assertFalse(consultantDao.checkCredentials(consultantNotPersisted.getIdentificationNumber(), consultantNotPersisted.getPassword()));
	}
	
	@Test
	public void testGetConsultantIdFromIdNumber() {
		Assertions.assertEquals(consultant.getId(), consultantDao.getConsultantIdFromIdNumber(consultant.getIdentificationNumber()));
		Assertions.assertThrows(NoResultException.class, ()->{
			consultantDao.getConsultantIdFromIdNumber("testUser@example.com");
		});
	}
	
	@Test
	public void testGetAssociatedUsers() {
		Consultant consultantTest = new Consultant(UUID.randomUUID().toString());
		Assertions.assertEquals(consultant.getUsers().size(), consultantDao.getAssociatedUsers(consultant.getId()).size());
		Assertions.assertTrue(consultantDao.getAssociatedUsers(consultant.getId()).containsAll(consultant.getUsers()));
		Assertions.assertThrows(NoResultException.class, ()->{
			consultantDao.getAssociatedUsers(consultantTest.getId());
		});
	}
	
	@Test
	public void testModifyAccountType() { 
		BankAccountType newType = BankAccountType.INVESTITORE;
		consultantDao.modifyAccountType(newType, account.getId());
		BankAccount manuallyRetrievedAccount = entityManager.
        		createQuery("FROM BankAccount WHERE uuid = :uuid", BankAccount.class)
        		.setParameter("uuid", account.getUuid())
        		.getSingleResult();
		Assertions.assertEquals(newType, manuallyRetrievedAccount.getType());
	}
	
	@Test
	public void testGetAllConsultantsIds() {
		List<Long> result = entityManager.createQuery("select id from Consultant", Long.class).getResultList();
		Assertions.assertEquals(result.size(), consultantDao.getAllConsultantsIds().size());
		Assertions.assertTrue(result.containsAll(consultantDao.getAllConsultantsIds()));
	}

}
