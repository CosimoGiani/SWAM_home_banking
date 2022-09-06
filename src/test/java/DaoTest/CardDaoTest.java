package DaoTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dao.CardDao;
import model.BankAccount;
import model.Card;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;

public class CardDaoTest extends JPATest {
	
	private CardDao cardDao;
	private Card card;
	//private BankAccount account;
	
	@Override
	protected void init() throws IllegalAccessException {
		System.out.println("Avvio init custom per CardDaoTest");
		
		//account = new BankAccount(UUID.randomUUID().toString());
		//account.setType(BankAccountType.ORDINARIO);
		//account.setBalance(0);
		
		card = new Card(UUID.randomUUID().toString());
		//account.addCard(card);
		card.setCardNumber("7777 5555 3333 1111");
		card.setExpirationDate(LocalDate.now().plusYears(2));
		card.setMassimale((float) 1500);
		card.setCardType(CardType.DEBITO);
		card.setActive(true);
		
		//entityManager.persist(account);
		entityManager.persist(card);
		cardDao = new CardDao();                                                   
        FieldUtils.writeField(cardDao, "em", entityManager, true);
	}
	
	@Test
    public void testSave() {                                                            
        Card cardToPersist = new Card(UUID.randomUUID().toString());
        cardToPersist.setCardNumber("4444 5555 3333 1111");
        cardToPersist.setExpirationDate(LocalDate.now().plusYears(2));
        cardToPersist.setMassimale((float) 3500);
        cardToPersist.setCardType(CardType.CREDITO);
        cardToPersist.setActive(true);
        cardDao.save(cardToPersist);                                             
        Card manuallyRetrievedCard = entityManager.
                createQuery("FROM Card WHERE uuid = :uuid", Card.class)
                .setParameter("uuid", cardToPersist.getUuid())           	           
                .getSingleResult();
        Assertions.assertEquals(cardToPersist, manuallyRetrievedCard);
    }
	
	@Test
	public void testBlockCard() {
		cardDao.blockCard(card.getId());
		Card manuallyRetrievedCard = entityManager.
        		createQuery("FROM Card WHERE uuid = :uuid", Card.class)
        		.setParameter("uuid", card.getUuid())
        		.getSingleResult();
		Assertions.assertEquals(false, manuallyRetrievedCard.isActive());
	}
	
	@Test
	public void testRemoveCard() {
		List<Card> retrievedCards = entityManager.createQuery("FROM Card WHERE id = :id", Card.class)
				.setParameter("id", card.getId())
				.getResultList();
		Assertions.assertFalse(retrievedCards.isEmpty());
		cardDao.removeCard(card.getId());
		retrievedCards = entityManager.createQuery("FROM Card WHERE id = :id", Card.class)
				.setParameter("id", card.getId())
				.getResultList();
		Assertions.assertTrue(retrievedCards.isEmpty());
	}
	
	@Test
	public void testUpdateMassimale() {
		cardDao.updateMassimale(card.getId(), (float) 2000);
		Card manuallyRetrievedCard = entityManager.
                createQuery("FROM Card WHERE uuid = :uuid", Card.class)
                .setParameter("uuid", card.getUuid())           	           
                .getSingleResult();
		Assertions.assertEquals((float) 2000, manuallyRetrievedCard.getMassimale());
	}

}
