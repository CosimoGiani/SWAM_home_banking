package rest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.BankAccountDao;
import dao.CardDao;
import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Card;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;

@Model
public class ConsultantController {
	
	@Inject
	private ConsultantDao consultantDao;
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private BankAccountDao accountDao;
	
	@Inject
	private CardDao cardDao;
	
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
	
	public BankAccount getBankAccountOwnedByUser(Long accountId, Long userId) throws Exception {
		List<BankAccount> accounts = userDao.getAssociatedBankAccounts(userId);
		if (accounts.isEmpty())
			throw new Exception();
		for (BankAccount account: accounts) {
			if (account.getId().equals(accountId)) {
				return account;
			}
		}
		return null;
	}
	
	public void modifyAccountType(BankAccountType type, Long id) {
		consultantDao.modifyAccountType(type, id);
	}
	
	public List<Transaction> getBankAccountTransactions(Long accountId) {
		List<Transaction> transactions = accountDao.getBankAccountTransactions(accountId);
		if (transactions.isEmpty())
			return null;
		return transactions;
	}
	
	public BankAccount getBankAccountLazy(Long accountId, Long userId) throws Exception {
		List<BankAccount> accounts = userDao.getAssociatedBankAccounts(userId);
		if (accounts.isEmpty())
			throw new Exception();
		for (BankAccount account: accounts) {
			if (account.getId().equals(accountId)) {
				BankAccount accountToReturn = accountDao.getAccountById(account.getId());
				return accountToReturn;
			}
		}
		return null;
	}
	
	public void addNewCard(BankAccount account, String cardNumber, float massimale, CardType cardType) throws Exception {
		Card card = new Card(UUID.randomUUID().toString());
		account.addCard(card);
		card.setCardNumber(cardNumber);
		card.setExpirationDate(LocalDate.now().plusYears(2));
		card.setMassimale(massimale);
		card.setCardType(cardType);
		card.setActive(true);
		cardDao.save(card);
		accountDao.update(account);
	}
	
	public void removeCard(Long cardId) {
		cardDao.removeCard(cardId);
	}
	
	public void updateMassimale(Long cardId, float massimale) {
		cardDao.updateMassimale(cardId, massimale);
	}

}
