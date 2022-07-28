package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;

import model.BankAccount;
import model.Card;
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
	
	@Transactional
	public void update(BankAccount account) {
		em.unwrap(Session.class).update(account);
	}
	
	public List<Transaction> getBankAccountTransactions(Long id) {
		List<Transaction> result = em.createQuery("from Transaction where account_id = :id", Transaction.class)
									 .setParameter("id", id)
									 .getResultList();
		return result;
	}
	
	public List<Card> getBankAccountCards(Long id) {
		List<Card> result = em.createQuery("from Card where account_id = :id", Card.class)
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
	
	public BankAccount getAccountById(Long id) {
		BankAccount account = em.createQuery("select distinct b from BankAccount b join fetch b.cards where b.id = :id", BankAccount.class)
								.setParameter("id", id)
								.getSingleResult();
		return account;
	}

}
