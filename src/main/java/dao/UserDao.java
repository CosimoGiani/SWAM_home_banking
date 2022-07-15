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
											+ "and password = :pass", User.class)
							  .setParameter("email", userData.getEmail())
							  .setParameter("pass", userData.getPassword())
							  .setMaxResults(1)
							  .getResultList();
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

}
