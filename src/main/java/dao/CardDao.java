package dao;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.QueryException;

import model.Card;

@RequestScoped
public class CardDao implements Serializable {
	
	private static final long serialVersionUID = 1286448513455821377L;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void save(Card card) {
		// em.getTransaction().begin();
		em.persist(card);
	}
	
	@Transactional
	public boolean blockCard(Long card_id) {
		int rowsUpdated = em.createQuery("update Card set isActive = :active where id = :card_id")
				.setParameter("active", false)
				.setParameter("card_id", card_id)
				.executeUpdate();
		if(rowsUpdated == 1)
			return true;
		else if (rowsUpdated == 0)
			return false;
		else
			throw new QueryException("Something is wrong in the Query");
	}
}
