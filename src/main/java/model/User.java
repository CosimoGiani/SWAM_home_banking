package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
	
	private String email;
	private String password;
	private String firstname;
	private String lastname;
	private String dateOfBirth;
	private String address;
	private String phoneNumber;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<BankAccount> bankAccounts;
	
	User() {}
	
	public User(String uuid) {
		super(uuid);
		this.bankAccounts = new ArrayList<BankAccount>();
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	// This method is used to set an encrypted password when a new user is created
	public void setEncryptedPassword(String password) {
		String encryptedPassword;
		
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(password.getBytes());
			byte[] bytes = m.digest();
			StringBuilder s = new StringBuilder();
			for (byte b: bytes) {
				s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			encryptedPassword = s.toString();
			setPassword(encryptedPassword);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}	
	}
	
	public List<BankAccount> getBankAccounts() {
		return bankAccounts;
	}
	
	public void setBankAccounts(List<BankAccount> bankAccounts) {
		this.bankAccounts = bankAccounts;
	}
	
	public void addBankAccountToList(BankAccount bankAccount) {
		this.bankAccounts.add(bankAccount);
	}

}
