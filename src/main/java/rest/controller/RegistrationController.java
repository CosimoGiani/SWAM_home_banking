package rest.controller;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import dao.BankAccountDao;
import dao.UserDao;
import model.BankAccount;
import model.User;
import model.enumeration.BankAccountType;
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
	
	@Transactional //altrimenti la save() dei DAO non funziona
	public Response createAccount(InputStream uploadedInputStream, String email, String password) {
		
		if(!email.contains("@")) 
			return Response.notAcceptable(null).entity("Invalid email").build();
		
		if(password.length() < 4) 
			return Response.notAcceptable(null).entity("Password is too short").build();
		
		try {  
			Map<String, Object> extractedData = pdfUtil.extractData(uploadedInputStream);
			
			if(userDao.isEmailInDB(email)) 
				return Response.notAcceptable(null).entity("There is already an account linked to this email").build();
				
			User newUser = createUser(email, password, extractedData);
			BankAccount newAccount = createBankAccount(newUser, (String) extractedData.get("selectedBankAccount"));
			
			userDao.save(newUser);
			accountDao.save(newAccount);
			
			System.out.println("Account created successfully!");
			System.out.println("email: "+email);
			System.out.println("firstname: "+extractedData.get("name"));
			System.out.println("secondname: "+extractedData.get("surname"));
			
			return Response.status(200).entity("Account created successfully").build();
			
		} catch (ParseException e) {
			return Response.notAcceptable(null).entity("Wrong bithday date").build();
			
		} catch (IllegalArgumentException e)	{
			return Response.notAcceptable(null).entity(e.getMessage()).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Internal Error").build();
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
		
		if(selectedBankAccount == "Ordinario") {
			account.setType(BankAccountType.ORDINARIO);
		} else if(selectedBankAccount == "Under30") {
			account.setType(BankAccountType.UNDER30);
		} else if(selectedBankAccount == "Investitore") {
			account.setType(BankAccountType.INVESTITORE);
		} else {
			throw new IllegalArgumentException("This Bank Account does not exist!");
		}
		
		return account;
	}
	
}