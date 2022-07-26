package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.UserDao;
import model.BankAccount;

@Model
public class UserController {
	
	@Inject
	private UserDao userDao;
	
	public List<BankAccount> getAssociatedBankAccounts(String email) {
		Long user_id = userDao.getUserIdFromEmail(email);
		return userDao.getAssociatedBankAccounts(user_id);
	}

}
