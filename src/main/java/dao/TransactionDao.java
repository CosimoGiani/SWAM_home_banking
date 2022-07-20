package dao;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import model.Transaction;

@RequestScoped
public class TransactionDao implements Serializable {

	private static final long serialVersionUID = 228613149399362203L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(Transaction transaction) {
		// em.getTransaction().begin();
		em.persist(transaction);
	}
	

}
