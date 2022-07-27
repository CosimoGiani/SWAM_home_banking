package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.ConsultantDao;
import model.User;

@Model
public class ConsultantController {
	
	@Inject
	private ConsultantDao consultantDao;
	
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

}
