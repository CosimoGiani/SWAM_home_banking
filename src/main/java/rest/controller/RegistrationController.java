package rest.controller;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import dao.BankAccountDao;
import dao.UserDao;
import model.BankAccount;
import model.User;
import pdf.PdfUtil;

@Model
public class RegistrationController {
	
	@Inject
	private UserDao userDao;
	@Inject
	private PdfUtil pdfUtil;
	@Inject
	private BankAccountDao accountDao;
	
	public File getPdf() {
		return pdfUtil.getPdf();
	}
	
	public Response createAccount(InputStream uploadedInputStream, String email, String password) {
		System.out.println(password);
		if(!email.contains("@")) 
			return Response.notAcceptable(null).entity("Invalid email").build();
		
		if(password.length() < 4) 
			return Response.notAcceptable(null).entity("Password is too short").build();
		
		try {  
			Map<String, Object> extractedData = pdfUtil.extractData(uploadedInputStream);
			
			if(!userDao.isEmailInDB(email)) 
				return Response.notAcceptable(null).entity("There is already an account linked to this email").build();
				
			User newUser = createUser(email, password, extractedData);
			BankAccount newAccount = createBankAccount(newUser, (String) extractedData.get("selectedBankAccount"));
			
			userDao.save(newUser);
			accountDao.save(newAccount);
			
			return Response.status(200).entity("Account").build();
			
		} catch (ParseException e) {
			return Response.notAcceptable(null).entity("Wrong bithday date").build();
			
		} catch (IllegalArgumentException e)	{
			return Response.notAcceptable(null).entity(e.getMessage()).build();
			
		} catch (Exception e) {
			return Response.status(500).entity("Error handling PDF").build();
		}
	}
	
	private User createUser(String email, String password, Map<String, Object> extractedData) {
		User user = new User(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setEncryptedPassword(password);
		user.setFirstname((String)extractedData.get("name"));
		user.setLastname((String)extractedData.get("surname"));
		user.setDateOfBirth((LocalDate)extractedData.get("birthDate"));
		user.setAddress((String)extractedData.get("address"));
		user.setCity((String)extractedData.get("city"));
		user.setProvince((String)extractedData.get("province"));
		user.setPhoneNumber((String)extractedData.get("phone"));
		return user;
	}
	
	private BankAccount createBankAccount(User user, String selectedBankAccount) {
		BankAccount account = new BankAccount(UUID.randomUUID().toString());
		user.addBankAccountToList(account);
		return account;
	}
	
}