package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import model.BankAccount;
import model.Transaction;

@RequestScoped
public class BankAccountDao implements Serializable {

	private static final long serialVersionUID = -8540805408602006004L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(BankAccount account) {
		em.persist(account);
	}
	
	public List<Transaction> getBankAccountTransactions(Long id) {
		List<Transaction> result = em.createQuery("from Transaction where account_id = :id", Transaction.class)
									 .setParameter("id", id)
									 .getResultList();
		return result;
	}
	
	public boolean isBankAccountInDB(Long id) {
		List<BankAccount> result = em.createQuery("from BankAccount where id = :id", BankAccount.class)
									 .setParameter("id", id)
									 .setMaxResults(1)
									 .getResultList();
		if(result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	@Transactional
	public void deleteBankAccount(Long id) {
		em.createQuery("delete from BankAccount where id = :id").setParameter("id", id).executeUpdate();
	}

}
