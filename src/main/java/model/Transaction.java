package model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

import model.enumeration.TransactionType;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity{
	
	private float amount;
	private LocalDate date;
	private String location;
	private TransactionType transactionType;
	
	Transaction(){};
	
	public Transaction(String uuid){
		super(uuid);
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

}
