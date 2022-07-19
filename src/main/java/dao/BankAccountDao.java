package dao;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import model.BankAccount;

@RequestScoped
public class BankAccountDao implements Serializable {

	private static final long serialVersionUID = -8540805408602006004L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(BankAccount account) {
		em.persist(account);
	}

}
