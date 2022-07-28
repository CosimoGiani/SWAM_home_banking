package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import model.BankAccount;
import model.User;

//@SuppressWarnings("serial")
@RequestScoped
public class UserDao implements Serializable {

	private static final long serialVersionUID = 6101943130460825403L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(User user) {
		em.persist(user);
	}
	
	public User login(User userData) {
		List<User> result = em.createQuery("from User "
											+ "where email = :email "
											+ "and password = :password", User.class)
							  .setParameter("email", userData.getEmail())
							  .setParameter("password", userData.getPassword())
							  .setMaxResults(1)
							  .getResultList();
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public boolean isEmailInDB(String email) {
		List<User> result = em.createQuery("from User where email = :email ", User.class)
							  .setParameter("email", email)
							  .setMaxResults(1)
							  .getResultList();
		
		if(result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public User getUserFromEmail(String email) {
		List<User> result = em.createQuery("from User where email = :email ", User.class)
							  .setParameter("email", email)
							  .setMaxResults(1)
							  .getResultList();
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public boolean checkCredentials(String email, String encryptedPassword) {
		List<User> result = em.createQuery("from User where email = :email and password = :password", User.class)
							  .setParameter("email", email)
							  .setParameter("password", encryptedPassword)
							  .setMaxResults(1)
							  .getResultList();

		if(result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public Long getUserIdFromEmail(String email) {
		Long user_id = em.createQuery("select id from User where email = :email", Long.class)
		         .setParameter("email", email)
		         .getSingleResult();

		if(user_id == null)
			throw new NotFoundException("No user connected to this email");
		
		return user_id;
	}
	
	public Long getConsultantIdFromEmail(String email) {
		Long consultant_id = em.createQuery("select distinct c.id from User u left join u.consultant c where u.email = :email", Long.class)
		         .setParameter("email", email)
		         .getSingleResult();

		if(consultant_id == null)
			throw new NotFoundException("No consultant is associated to this user");
		
		return consultant_id;
	}
	
	public List<BankAccount> getAssociatedBankAccounts(Long user_id) {
		
		List<BankAccount> result = em.createQuery("from BankAccount where user_id = :user_id", BankAccount.class)
									 .setParameter("user_id", user_id)
									 .getResultList();
		// System.out.println(result.get(0).getUuid());
		if(result.isEmpty())
			throw new NotFoundException("No Bank Accounts are associated to this User");
		
		return result;
	}
	
}
