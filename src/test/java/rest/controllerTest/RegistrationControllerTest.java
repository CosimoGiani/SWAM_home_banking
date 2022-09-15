package rest.controllerTest;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.BankAccountDao;
import dao.ConsultantDao;
import dao.UserDao;
import model.Consultant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import pdf.PdfUtil;
import rest.controller.RegistrationController;

public class RegistrationControllerTest {
	
	private RegistrationController registrationController;
	private PdfUtil pdfUtil;
	private UserDao userDao;
	private ConsultantDao consultantDao;
	private BankAccountDao accountDao;
	private Consultant consultant;
	private Map<String, Object> extractedData;

	@BeforeEach
	public void setup() throws IllegalAccessException {
		registrationController = new RegistrationController();
		pdfUtil = mock(PdfUtil.class);
		userDao = mock(UserDao.class);
		consultantDao = mock(ConsultantDao.class);
		consultant = mock(Consultant.class);
		accountDao = mock(BankAccountDao.class);
		extractedData = new HashMap<String, Object>();
		extractedData.put("name", "Mario");
		extractedData.put("surname", "Rossi");
		extractedData.put("birthDate", LocalDate.of(Integer.parseInt("1995"), Integer.parseInt("07"), Integer.parseInt("25")));
		extractedData.put("address", "Via Roma");
		extractedData.put("city", "Firenze");
		extractedData.put("province", "Firenze");
		extractedData.put("phone", "3349959603");
		extractedData.put("selectedBankAccount", "Ordinario");
		FieldUtils.writeField(registrationController, "pdfUtil", pdfUtil, true);
		FieldUtils.writeField(registrationController, "userDao", userDao, true);
		FieldUtils.writeField(registrationController, "consultantDao", consultantDao, true);
		FieldUtils.writeField(registrationController, "accountDao", accountDao, true);
	}
	
	@Test
	public void testCreateAccount() throws IOException, IllegalArgumentException, NumberFormatException {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(pdfUtil.extractData(uploadedInputStream)).thenReturn(extractedData);
		when(consultant.getId()).thenReturn(1L);
		when(consultantDao.getConsultantEager(consultant.getId())).thenReturn(consultant);
		List<Long> ids = new ArrayList<Long>();
		ids.add(consultant.getId());
		when(consultantDao.getAllConsultantsIds()).thenReturn(ids);
		assertEquals("Account created successfully", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
	@Test
	public void testCreateAccountWhenEmailIsInvalid() {
		InputStream uploadedInputStream = mock(InputStream.class);
		String invalidEmail = "user1.example.com";
		String password = "1234";
		assertEquals("Invalid email", registrationController.createAccount(uploadedInputStream, invalidEmail, password));
	}
	
	@Test
	public void testCreateAccountWhenPasswordIsTooShort() {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String invalidPassword = "123";
		assertEquals("Password is too short", registrationController.createAccount(uploadedInputStream, email, invalidPassword));
	}
	
	@Test
	public void testCreateAccountWhenEmailIsAlreadyLinkedToAccount() {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(userDao.isEmailInDB(email)).thenReturn(true);
		assertEquals("There is already an account linked to this email", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
	@Test
	public void testCreateAccountWhenNumberFormatExceptionIsThrown() throws IOException {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(pdfUtil.extractData(uploadedInputStream)).thenThrow(new NumberFormatException());
		assertEquals("Wrong birthday date", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
	@Test
	public void testCreateAccountWhenIllegalArgumentExceptionIsThrown() throws IOException {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(pdfUtil.extractData(uploadedInputStream)).thenThrow(new IllegalArgumentException("Name or Surname contains a number!"));
		assertEquals("Name or Surname contains a number!", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
	@Test
	public void testCreateAccountWhenUploadedDocumentIsIncorrect() throws IOException {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(pdfUtil.extractData(uploadedInputStream)).thenThrow(new com.spire.pdf.packages.sprlMc("The uploaded document is incorrect"));
		assertEquals("The uploaded document is incorrect", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
	@Test
	public void testCreateAccountWhenNoResult() throws IOException {
		InputStream uploadedInputStream = mock(InputStream.class);
		String email = "user1@example.com";
		String password = "1234";
		when(pdfUtil.extractData(uploadedInputStream)).thenThrow(new NoResultException("Internal Error"));
		assertEquals("Internal Error", registrationController.createAccount(uploadedInputStream, email, password));
	}
	
}
