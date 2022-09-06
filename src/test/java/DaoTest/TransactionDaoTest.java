package DaoTest;

import java.time.LocalDate;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dao.TransactionDao;
import model.Transaction;
import model.enumeration.TransactionType;

public class TransactionDaoTest extends JPATest {
	
	private TransactionDao transactionDao;
	private Transaction transaction;
	
	@Override
	protected void init() throws IllegalAccessException {
		System.out.println("Avvio init custom per TransactionDaoTest");
		
		transaction = new Transaction(UUID.randomUUID().toString());
		transaction.setAmount((float) 500);
		transaction.setTransactionType(TransactionType.VERSAMENTO);
		transaction.setDate(LocalDate.now().minusMonths(2));
		transaction.setLocation("ATM 3");
		
		entityManager.persist(transaction);
		transactionDao = new TransactionDao();
		FieldUtils.writeField(transactionDao, "em", entityManager, true);
	}
	
	@Test
	public void testSave() {
		Transaction transactionToPersist = new Transaction(UUID.randomUUID().toString());
		transactionToPersist.setAmount((float) 300);
		transactionToPersist.setTransactionType(TransactionType.PAGAMENTO);
		transactionToPersist.setDate(LocalDate.now().minusWeeks(3));
		transactionToPersist.setLocation("Coop");
        transactionDao.save(transactionToPersist);                                             
        Transaction manuallyRetrievedTransaction = entityManager.
                createQuery("FROM Transaction WHERE uuid = :uuid", Transaction.class)
                .setParameter("uuid", transactionToPersist.getUuid())           	           
                .getSingleResult();
        Assertions.assertEquals(transactionToPersist, manuallyRetrievedTransaction);
	}

}
