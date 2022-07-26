package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.BankAccountDao;
import dao.UserDao;
import model.Transaction;

@Model
public class BankAccountController {
	
	@Inject
	private BankAccountDao bankAccountDao;
	
	@Inject
	private UserDao userDao;
	
	public List<Transaction> getBankAccountTransactions(Long id) throws Exception {
		List<Transaction> transactions = bankAccountDao.getBankAccountTransactions(id);
		if (transactions.isEmpty()) {
			throw new Exception();
		}
		return transactions;
	}
	
	public void deleteBankAccount(Long id) throws Exception {
		boolean exists = bankAccountDao.isBankAccountInDB(id);
		if (exists) {
			bankAccountDao.deleteBankAccount(id);
		} else {
			throw new Exception();
		}
	}
	
	public boolean userOwnsBankAccount(String email, Long id) {
		Long userId = userDao.getUserIdFromEmail(email);
		List<?> list = userDao.getAssociatedBankAccounts(userId);
		//System.out.println("Il tipo è: " + list.get(0).getClass());
		return true;
	}

}
