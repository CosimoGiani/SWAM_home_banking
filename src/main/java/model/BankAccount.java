package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import model.enumeration.BankAccountType;

@Entity
@Table(name = "bankAccounts")
public class BankAccount extends BaseEntity {
	
	private String accountNumber;
	private float balance;
	private String iban;
	//private List<String> transactions;	// dovremo poi passare la classe transazioni
	private BankAccountType type;
	//private List<String> cards;			// anche qui quando si crea la classe carta
	
	@Transient
	private BankAccountDetails details;
	
	BankAccount() {}
	
	public BankAccount(String uuid) {
		super(uuid);
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public BankAccountType getType() {
		return type;
	}

	public void setType(BankAccountType type) {
		this.type = type;
		this.details = new BankAccountDetails(type);
	}

	/*public List<String> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<String> transactions) {
		this.transactions = transactions;
	}

	public List<String> getCards() {
		return cards;
	}

	public void setCards(List<String> cards) {
		this.cards = cards;
	}
	*/

}
