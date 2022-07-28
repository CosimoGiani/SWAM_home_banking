package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;

import model.Consultant;
import model.User;
import model.enumeration.BankAccountType;

@RequestScoped
public class ConsultantDao implements Serializable {

	private static final long serialVersionUID = 1684920672521287622L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(Consultant consultant) {
		em.persist(consultant);
	}
	
	@Transactional
	public void update(Consultant consultant) {
		em.unwrap(Session.class).update(consultant);
	}
	
	public boolean checkCredentials(String identificationNumber, String encryptedPassword) {
		List<Consultant> result = em.createQuery("from Consultant where identificationNumber = :identificationNumber and password = :password", Consultant.class)
							  .setParameter("identificationNumber", identificationNumber)
							  .setParameter("password", encryptedPassword)
							  .setMaxResults(1)
							  .getResultList();
		if(result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public Long getConsultantIdFromIdNumber(String identificationNumber) {
		Long id = em.createQuery("select id from Consultant where identificationNumber = :identificationNumber", Long.class)
		         .setParameter("identificationNumber", identificationNumber)
		         .getSingleResult();
		return id;
	}
	
	public List<User> getAssociatedUsers(Long consultant_id) {
		List<User> result = em.createQuery("from User where consultant_id = :consultant_id", User.class)
				 			  .setParameter("consultant_id", consultant_id)
				 			  .getResultList();
		return result;
	}
	
	public User getUserFromId(Long id) {
		User user = em.createQuery("from User where id = :id", User.class)
					  .setParameter("id", id)
					  .getSingleResult();
		return user;
	}
	
	@Transactional
	public void modifyAccountType(BankAccountType type, Long id) {
		em.createQuery("update BankAccount set type = :type where id = :id")
		  .setParameter("type", type)
		  .setParameter("id", id)
		  .executeUpdate();
	}
	
	public List<Long> getAllConsultantsIds(){
		List<Long> result = em.createQuery("select id from Consultant", Long.class)
									.getResultList();
		return result;
	}
	
	public Consultant getConsultantEager(Long consultant_id) {
		Consultant consultant = em.createQuery("select distinct c from Consultant c left join fetch c.users where c.id = :consultant_id", Consultant.class)
								  .setParameter("consultant_id", consultant_id)
								  .getSingleResult();
		return consultant;
	}
	
	public Consultant getConsultantLazy(Long consultant_id, boolean obscureSensible) {
		Consultant consultant = em.createQuery("from Consultant c where c.id = :consultant_id", Consultant.class)
				  .setParameter("consultant_id", consultant_id)
				  .getSingleResult();
		if(obscureSensible) {
			consultant.setPassword(null);
		}
		return consultant;
	}

}
