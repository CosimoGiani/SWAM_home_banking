package rest.controller;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.UserDao;
import model.User;

@Model
public class LoginController {
	
	@Inject
	private UserDao userDao;
	
	public Long login(User loggingUser) {
		User loggedUser = userDao.login(loggingUser);
		if( loggedUser == null ) {
			return null;
		} 
		return loggedUser.getId();
	}

}
