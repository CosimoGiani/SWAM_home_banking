package utils;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;

import dao.BankAccountDao;
import dao.UserDao;
import model.BankAccount;
import model.User;
import model.enumeration.BankAccountType;

@Singleton
@Startup
public class LoadMocks {
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private BankAccountDao accountDao;
	
	//private BankAccountType type = ;
	
	@PostConstruct
	@Transactional
	public void init(){
		
		System.out.println("initializing database.. ");
		User user1 = createUser("user1@example.com", "pass1");
		User user2 = createUser("user2@example.com", "pass2");
		
		BankAccount account1 = createBankAccount(user1);
		
		userDao.save(user1);
		userDao.save(user2);
		
		accountDao.save(account1);

		System.out.println(".. database initialized! ");
		
	}
	
	private User createUser(String email, String password) {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		return user;
	}
	
	private BankAccount createBankAccount(User user) {
		BankAccount account = new BankAccount(UUID.randomUUID().toString());
		user.addBankAccountToList(account);
		account.setType(BankAccountType.ORDINARIO);
		return account;
	}

}
