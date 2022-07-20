package model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

import model.enumeration.CardType;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {
	
	private String cardNumber;
	private LocalDate expirationDate;
	private float massimale;
	private CardType cardType;
	
	Card() {}
	
	public Card(String uuid) {
		super(uuid);
	}

	public String getCardNumber() {
		return cardNumber;
		
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
		
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
		
	}

	public float getMassimale() {
		return massimale;
		
	}

	public void setMassimale(float massimale) {
		this.massimale = massimale;
		
	}

	public CardType getCardType() {
		return cardType;
		
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
		
	}
}
