package rest.controller;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import dao.BankAccountDao;
import dao.ConsultantDao;
import dao.UserDao;
import model.BankAccount;
import model.Consultant;
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
	@Inject
	private ConsultantDao consultantDao;
	
	public File getPdf() {
		return pdfUtil.getPdf();
	}
	
	@Transactional //altrimenti la save() dei DAO non funziona
	public String createAccount(InputStream uploadedInputStream, String email, String password) {
		
		if(!email.contains("@")) 
			return "Invalid email";
		
		if(password.length() < 4) 
			return "Password is too short";
		
		try {  
			Map<String, Object> extractedData = pdfUtil.extractData(uploadedInputStream);
			
			if(userDao.isEmailInDB(email)) 
				return "There is already an account linked to this email";
				
			User newUser = createUser(email, password, extractedData);
			BankAccount newAccount = createBankAccount(newUser, (String) extractedData.get("selectedBankAccount"));
			
			Long consultant_selected_id = getConsultantWithFewerUsers();
			Consultant selectedConsultant = consultantDao.getConsultantEager(consultant_selected_id);
			
			newUser.setConsultant(selectedConsultant);
			selectedConsultant.addUser(newUser);
			
			userDao.save(newUser);
			accountDao.save(newAccount);
			consultantDao.update(selectedConsultant);
			
			System.out.println("Account created successfully!");
			System.out.println("email: "+email);
			System.out.println("firstname: "+extractedData.get("name"));
			System.out.println("secondname: "+extractedData.get("surname"));
			
			return "Account created successfully";
			
		} catch (NumberFormatException e) {
			return "Wrong birthday date";
			
		} catch (IllegalArgumentException e)	{
			return e.getMessage();
	
		} catch(com.spire.pdf.packages.sprlMc e) {
			System.out.println("Il documento caricato non è quello corretto");
			return "The uploaded document is incorrect";
			
		} catch (NoResultException e) {
			// e.printStackTrace();
			System.out.println("Nessun Consulente è disponibile...");
			return "Internal Error";
			
		} catch (Exception e) {
			// System.out.println(e.getClass().toString());
			e.printStackTrace();
			return "Internal Error";
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
	
	private Long getConsultantWithFewerUsers() {
		/* 
		 * Per prendere il Consultant che ha meno Users associati
		 */
		
		List<Long> consultantIds = consultantDao.getAllConsultantsIds();
		
		
		Long consultant_selected = Long.valueOf(0); 
		int minUsersAssociated = 10000000;
		int currentUsersAssociated;
		for(Long consultant_id : consultantIds) {
			try {
				currentUsersAssociated = consultantDao.getAssociatedUsers(consultant_id).size();
			} catch(NoResultException e) {
				currentUsersAssociated = 0;
			}
			if(currentUsersAssociated < minUsersAssociated) {
				consultant_selected = consultant_id;
				minUsersAssociated = currentUsersAssociated;
			}
		}
		
		return consultant_selected;
	}
	
}