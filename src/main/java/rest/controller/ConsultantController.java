package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Consultant;
import model.User;
import model.enumeration.BankAccountType;

@Model
public class ConsultantController {
	
	@Inject
	private ConsultantDao consultantDao;
	
	@Inject
	private UserDao userDao;
	
	public List<User> getAssociatedUsers(String identificationNumber, boolean obscurePassword) throws Exception {
		Long id = consultantDao.getConsultantIdFromIdNumber(identificationNumber);
		List<User> users = consultantDao.getAssociatedUsers(id);
		if (users.isEmpty())
			throw new Exception();
		if (obscurePassword) {
			for (User user: users)
				user.setPassword(null);
		}
		return users;
	}
	
	public Long getConsultantIdFromIdNumber(String identificationNumber) throws Exception {
		Long id = consultantDao.getConsultantIdFromIdNumber(identificationNumber);
		if (id == null)
			throw new Exception();
		return id;
	}
	
	public User getUserDetails(Long user_id,  boolean obscurePassword) throws Exception {
		User user = consultantDao.getUserFromId(user_id);
		if (user == null)
			throw new Exception();
		if (obscurePassword)
			user.setPassword(null);
		return user;
	}
	
	public boolean checkUserIsAssociated(Long consultantId, Long userId) {
		User user = consultantDao.getUserFromId(userId);
		if (user.getConsultant().getId() == consultantId)
			return true;
		else return false;
	}
	
	public List<BankAccount> getUserBankAccounts(Long userId) {
		return userDao.getAssociatedBankAccounts(userId);
	}
	
	public BankAccount getBankAccountOwnedByUser(Long accountId, Long userId) {
		List<BankAccount> accounts = userDao.getAssociatedBankAccounts(userId);
		BankAccount accountOwned = null;
		for (BankAccount account: accounts) {
			if (account.getId().equals(accountId)) {
				accountOwned = account;
				return accountOwned;
			}
		}
		return accountOwned;
	}
	
	public void modifyAccountType(BankAccountType type, Long id) {
		consultantDao.modifyAccountType(type, id);
	}

}
