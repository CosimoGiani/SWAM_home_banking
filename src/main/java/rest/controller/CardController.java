package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.BankAccountDao;
import dao.CardDao;
import dao.UserDao;
import model.BankAccount;
import model.Card;

@Model
public class CardController {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private BankAccountDao bankAccountDao;
	
	@Inject 
	private CardDao cardDao;
	
	public boolean userOwnsCard(String email, Long card_id) {
		Long user_id = userDao.getUserIdFromEmail(email);
		List<BankAccount> associatedBankAccounts = userDao.getAssociatedBankAccounts(user_id);
		
		List<Card> associatedCards;
		for(BankAccount b : associatedBankAccounts) {
			associatedCards = bankAccountDao.getBankAccountCards(b.getId());
			for(Card c : associatedCards) {
				if(card_id == c.getId())
					return true;
			}
		}
		return false;
	}
	
	public boolean blockCard(Long card_id) {
		return cardDao.blockCard(card_id);
	}

}
