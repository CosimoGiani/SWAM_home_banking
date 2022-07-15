package utils;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;

import dao.UserDao;
import model.User;

@Singleton
@Startup
public class LoadMocks {
	
	@Inject
	private UserDao userDao;
	
	@PostConstruct
	@Transactional
	public void init(){
		//userDao.deleteUsers();		
		
		System.out.println("initializing database.. ");
		User user1 = createUser("user1@example.com", "pass1");
		User user2 = createUser("user2@example.com", "pass2");
		
		userDao.save(user1);
		userDao.save(user2);

		System.out.println(".. database initialized! ");
		
	}
	
	private User createUser(String email, String password) {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		return user;
	}

}
