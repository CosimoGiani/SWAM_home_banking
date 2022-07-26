package model;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import model.enumeration.BankAccountType;

@Entity
@Table(name = "bankAccounts")
public class BankAccount extends BaseEntity {
	
	private String accountNumber;
	private float balance;
	private String iban;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Transaction> transactions;	
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Card> cards;	

	private BankAccountType type;
	
	@Transient
	private BankAccountDetails details;
	
	BankAccount() {}
	
	public BankAccount(String uuid) {
		super(uuid);
		this.transactions = new ArrayList<Transaction>();
		this.cards = new ArrayList<Card>();
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
	
	@JsonbTransient
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}
	
	@JsonbTransient
	public List<Card> getCards() {
		return cards;
	}
	
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public void addCard(Card card){
		this.cards.add(card);
	}

}
