package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
	
}
