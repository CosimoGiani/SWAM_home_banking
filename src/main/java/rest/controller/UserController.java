package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.UserDao;
import model.BankAccount;
import model.User;

@Model
public class UserController {
	
	@Inject
	private UserDao userDao;
	
	public List<BankAccount> getAssociatedBankAccounts(String email) {
		Long user_id = userDao.getUserIdFromEmail(email);
		return userDao.getAssociatedBankAccounts(user_id);
	}
	
	public User getUserFromEmail(String email, boolean obscurePassword) {
		User user =  userDao.getUserFromEmail(email);
		if(obscurePassword) {
			user.setPassword(null);
		}
		return user;
	}

}
