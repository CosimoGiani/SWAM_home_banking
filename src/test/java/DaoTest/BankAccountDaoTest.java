package DaoTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.TransientObjectException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dao.BankAccountDao;
import model.BankAccount;
import model.Card;
import model.Transaction;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import model.enumeration.TransactionType;

public class BankAccountDaoTest extends JPATest {
	
	BankAccountDao bankAccountDao;
	private BankAccount account; 
	
	@Override
	protected void init() throws IllegalAccessException {
        
        account = new BankAccount(UUID.randomUUID().toString());
		account.setType(BankAccountType.ORDINARIO);
		account.setBalance(350);
		
		Transaction t1 = new Transaction(UUID.randomUUID().toString());
		account.addTransaction(t1);
		t1.setAmount((float) 500);
		t1.setTransactionType(TransactionType.VERSAMENTO);
		t1.setDate(LocalDate.now().minusMonths(2));
		
		Transaction t2 = new Transaction(UUID.randomUUID().toString());
		account.addTransaction(t2);
		t2.setAmount((float) 300);
		t2.setTransactionType(TransactionType.PAGAMENTO);
		t2.setDate(LocalDate.now().minusMonths(1));
		
		Card card = new Card(UUID.randomUUID().toString());
        account.addCard(card);
		card.setCardNumber("1234123412341234");
		card.setExpirationDate(LocalDate.now().plusYears(2));
		card.setMassimale(1000);
		card.setCardType(CardType.DEBITO);
		card.setActive(true);
		
		entityManager.persist(account); 
	
		bankAccountDao = new BankAccountDao();
        FieldUtils.writeField(bankAccountDao, "em", entityManager, true);
	}
	
	@Test
	public void testSave() {
		BankAccount newBankAccount = new BankAccount(UUID.randomUUID().toString());
		newBankAccount.setType(BankAccountType.ORDINARIO);
		newBankAccount.setBalance(300);
		
        bankAccountDao.save(newBankAccount); 
        
        BankAccount manuallyRetrievedBankAccount = entityManager.
                createQuery("FROM BankAccount WHERE uuid = :uuid", BankAccount.class)
                .setParameter("uuid", newBankAccount.getUuid())           	            
                .getSingleResult();
        Assertions.assertEquals(newBankAccount, manuallyRetrievedBankAccount);    		
	}
	
	@Test 
	public void testUpdate() {
		
		account.setType(BankAccountType.UNDER30);
		
		bankAccountDao.update(account);                                                  
        BankAccount manuallyRetrievedBankAccount = entityManager.
                createQuery("FROM BankAccount WHERE uuid = :uuid", BankAccount.class)
                .setParameter("uuid", account.getUuid())           	           
                .getSingleResult();
        
        Assertions.assertEquals(account, manuallyRetrievedBankAccount);
        
        
        Card card = new Card(UUID.randomUUID().toString());
        account.addCard(card);
		card.setCardNumber("7777111100002222");
		card.setExpirationDate(LocalDate.now().plusYears(2));
		card.setMassimale(2000);
		card.setCardType(CardType.CREDITO);
		card.setActive(true);
		
		bankAccountDao.update(account);
		
		manuallyRetrievedBankAccount = entityManager.
                createQuery("FROM BankAccount WHERE uuid = :uuid", BankAccount.class)
                .setParameter("uuid", account.getUuid())           	           
                .getSingleResult();
        
        Assertions.assertEquals(account, manuallyRetrievedBankAccount);
        
        Assertions.assertThrows(TransientObjectException.class, ()->{
        	/* Se provo a fare update() di un BankAccount che non è persistito ricevo un'eccezione */
        	
        	BankAccount otherBankAccount = new BankAccount(UUID.randomUUID().toString());
        	otherBankAccount.setType(BankAccountType.ORDINARIO);
        	otherBankAccount.setBalance(0);
        	
        	bankAccountDao.update(otherBankAccount);
        });
	}
	
	@Test
	public void testGetBankAccountTransactions() {
		List<Transaction> retrievedTransactions = bankAccountDao.getBankAccountTransactions(account.getId());
		Assertions.assertEquals(account.getTransactions().size(), retrievedTransactions.size());
		Assertions.assertTrue(account.getTransactions().containsAll(retrievedTransactions));
	}
	
	@Test
	public void testGetBankAccountCards() {
		List<Card> retrievedCards = bankAccountDao.getBankAccountCards(account.getId());
		Assertions.assertEquals(account.getCards().size(), retrievedCards.size());
		Assertions.assertTrue(account.getCards().containsAll(retrievedCards));
	}
	
	@Test
	public void testIsBankAccountInDB() {
		Assertions.assertTrue(bankAccountDao.isBankAccountInDB(account.getId()));
		
		// Verifico che un account che non è persistito non si trovi effettivamente nel DB
		BankAccount newBankAccount = new BankAccount(UUID.randomUUID().toString());
		newBankAccount.setType(BankAccountType.ORDINARIO);
		newBankAccount.setBalance(0);
		
		Assertions.assertFalse(bankAccountDao.isBankAccountInDB(newBankAccount.getId()));
	}
	
	@Test
	public void testDeleteBankAccount() {
		
		List<BankAccount> retrievedAccounts = entityManager.createQuery("FROM BankAccount WHERE id = :id", BankAccount.class)
														   .setParameter("id", account.getId())
														   .getResultList();
		
		Assertions.assertFalse(retrievedAccounts.isEmpty());
		
		bankAccountDao.deleteBankAccount(account.getId());
		
		retrievedAccounts = entityManager.createQuery("FROM BankAccount WHERE id = :id", BankAccount.class)
										   .setParameter("id", account.getId())
										   .getResultList();

		Assertions.assertTrue(retrievedAccounts.isEmpty());
		
	}
	
	@Test
	public void testGetAccountById() {
		BankAccount retrievedAccount = bankAccountDao.getAccountById(account.getId());
		
		Assertions.assertEquals(account.getId(), retrievedAccount.getId());
		Assertions.assertEquals(account.getUuid(), retrievedAccount.getUuid());
		Assertions.assertEquals(account.getType(), retrievedAccount.getType());
		Assertions.assertEquals(account.getBalance(), retrievedAccount.getBalance());
		
	}
	
}
