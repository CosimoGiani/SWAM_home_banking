package controllerTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.BankAccountDao;
import dao.UserDao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import model.BankAccount;
import model.Card;
import model.User;
import rest.controller.CardController;

public class CardControllerTest {
	
	private CardController cardController;
	private Card mockedCard;
	private BankAccount mockedAccount;
	private UserDao userDao;
	private User user;
	private BankAccountDao bankAccountDao;
	
	@BeforeEach
	public void setup() throws IllegalAccessException {
		cardController = new CardController();
		mockedCard = mock(Card.class);
		mockedAccount = mock(BankAccount.class);
		userDao = mock(UserDao.class);
		user = new User(UUID.randomUUID().toString());
		user.setEmail("user1@example.com");
		user.setEncryptedPassword("password");
		bankAccountDao = mock(BankAccountDao.class);
		FieldUtils.writeField(cardController, "userDao", userDao, true);
		FieldUtils.writeField(cardController, "bankAccountDao", bankAccountDao, true);
	}
	
	@Test
	public void testUserOwnsCard() {
		List<BankAccount> accounts = new ArrayList<BankAccount>();
		accounts.add(mockedAccount);
		user.setBankAccounts(accounts);
		List<Card> cards = new ArrayList<Card>();
		cards.add(mockedCard);
		when(userDao.getUserIdFromEmail(user.getEmail())).thenReturn(1L);
		when(userDao.getAssociatedBankAccounts(1L)).thenReturn(user.getBankAccounts());
		when(mockedAccount.getId()).thenReturn(12L);
		when(bankAccountDao.getBankAccountCards(12L)).thenReturn(cards);
		when(mockedCard.getId()).thenReturn(123L);
		assertTrue(cardController.userOwnsCard(user.getEmail(), 123L));
		assertFalse(cardController.userOwnsCard(user.getEmail(), 45L));
	}

}
