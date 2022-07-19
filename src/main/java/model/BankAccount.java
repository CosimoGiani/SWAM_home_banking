package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bankAccounts")
public class BankAccount extends BaseEntity {
	
	public enum Type {
		Under30,
		Ordinario,
		Investitore
	}
	
	private String accountNumber;
	private float balance;
	private String iban;
	//private List<String> transactions;	// dovremo poi passare la classe transazioni
	private Type type;
	//private List<String> cards;			// anche qui quando si crea la classe carta
	
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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
