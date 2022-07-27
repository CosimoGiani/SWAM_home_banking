package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.CardDao;
import dao.UserDao;

@Model
public class CardController {
	
	@Inject
	private UserDao userDao;
	
	@Inject 
	private CardDao cardDao;
	
	public boolean userOwnsCard(String email, Long card_id) {
		return true;
	}
	
	public boolean blockCard(Long card_id) {
		return true;
	}

}
