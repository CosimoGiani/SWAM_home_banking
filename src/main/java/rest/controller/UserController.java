package rest.controller;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Consultant;
import model.User;

@Model
public class UserController {
	
	@Inject
	private UserDao userDao;
	@Inject
	private ConsultantDao consultantDao;
	
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
	
	public Consultant getConsultantAssociated(String userEmail) {
		Long consultant_id = userDao.getConsultantIdFromEmail(userEmail);
		return consultantDao.getConsultantLazy(consultant_id, true);
	}
	
	public String sendMessageToConsultant(String userEmail, String object, String corpus) {
		User user = userDao.getUserFromEmail(userEmail);
		String nameSender = user.getFirstname() + " " + user.getLastname();
		
		Long consultant_id = userDao.getConsultantIdFromEmail(userEmail);
		Consultant consultant = consultantDao.getConsultantLazy(consultant_id, true);
		
		String consultantEmail = consultant.getEmail();
		String nameReciever = consultant.getFirstname() + " " + consultant.getLastname();
		
		sendEmail(userEmail, consultantEmail, nameSender, nameReciever, object, corpus);
		
		return "Messaggio inviato correttamente";
	}
	
	private void sendEmail(String fromEmail, String toEmail, String nameSender, String nameReciever, String object, String corpus) {
		System.out.println("==========================================");
		System.out.println("MESSAGGIO da " + nameSender + " (" + fromEmail + ") per " + nameReciever + " (" + toEmail + ")" );
		System.out.println("OGGETTO: " + object);
		System.out.println(corpus);
		System.out.println("==========================================");
	}

}
