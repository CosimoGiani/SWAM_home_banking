package dao;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import model.Consultant;
import model.User;

@RequestScoped
public class ConsultantDao implements Serializable {

	private static final long serialVersionUID = 1684920672521287622L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(Consultant consultant) {
		em.persist(consultant);
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
		System.out.println("ID trovato è: " + id);
		/*if(id == null) {
			System.out.println("Nessun consulente corrisponde a questa matricola");
			throw new NotFoundException("Nessun consulente corrisponde a questa matricola");
		}*/
		return id;
	}
	
	public List<User> getAssociatedUsers(Long consultant_id) {
		List<User> result = em.createQuery("from User where consultant_id = :consultant_id", User.class)
				 			  .setParameter("consultant_id", consultant_id)
				 			  .getResultList();
		/*if(result.isEmpty()) {
			System.out.println("Non ci sono correntisti associati a questo consulente");
			throw new NotFoundException("Non ci sono correntisti associati a questo consulente");
		}*/
		return result;
	}

}
