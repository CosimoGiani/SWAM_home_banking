package model;

import model.enumeration.BankAccountType;

//@Entity
//@Table(name = "bankAccountsDetails")
public class BankAccountDetails {
	
	private float canoneMensile;
	private float tassoInteresse;
	private float massimale;
	
	public BankAccountDetails(BankAccountType type) {
		switch(type) {
		case ORDINARIO:
			this.canoneMensile = 1;
			this.tassoInteresse = (float) 0.8;
			this.massimale = 2000;
			break;
		case UNDER30:
			this.canoneMensile = 0;
			this.tassoInteresse = (float) 1.0;
			this.massimale = 500;
			break;
		case INVESTITORE:
			this.canoneMensile = 2;
			this.tassoInteresse = (float) 0.5;
			this.massimale = 5000;
			break;
		}
	}

	public float getCanoneMensile() {
		return canoneMensile;
	}

	public float getTassoInteresse() {
		return tassoInteresse;
	}

	public float getMassimale() {
		return massimale;
	}

}
