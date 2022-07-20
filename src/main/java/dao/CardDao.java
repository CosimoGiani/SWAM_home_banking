package dao;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
}
