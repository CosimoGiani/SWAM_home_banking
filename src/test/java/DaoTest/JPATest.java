package DaoTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class JPATest {
	
    private static EntityManagerFactory emFactory;
    protected EntityManager entityManager;
    
    @BeforeAll
    public static void setupEM() {
        System.out.println("Creazione EntityManagerFactory");
        emFactory = Persistence.createEntityManagerFactory("test");
    }
    
    @BeforeEach
    public void setup() throws IllegalAccessException {
        System.out.println("creazione EntityManager");
        entityManager = emFactory.createEntityManager(); 										// creo una nuova istanza di EntityManager
        entityManager.getTransaction().begin();          										// avvio la transazione di "pulitura"
        entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();   // da HSQLDB 2.2.6 in poi - ripulisce il DB mantenendo le tabelle
        entityManager.getTransaction().commit();        										// chiudo la transazione di "pulitura"
        entityManager.getTransaction().begin();         										// avvio la transazione per l'inizializzazione custom di init()
        System.out.println("invocazione metodo custom di init");
        init();                                         										// sarà definito nelle specializzazioni
        entityManager.getTransaction().commit();        										// chiudo la transazione relativa ad init()
        entityManager.clear();                          										// ripulisco EM da eventuali entità rimaste appese
        entityManager.getTransaction().begin();         										// avvio la transazione per l'uso che sarà fatto dal metodo di Test
        System.out.println("setup completato");
    }
    
    @AfterEach
    public void close(){
        if( entityManager.getTransaction().isActive()){
            entityManager.getTransaction().rollback();  		//se a questo ho transazioni attive ne faccio il rollback
        }
        System.out.println("Chiusura EntityManager");
        entityManager.close();                          		// chiudo definitivamente EM per questo test case
    }
    
    @AfterAll
    public static void tearDownDB() {
        System.out.println("Chiusura EntityManagerFactory");
        emFactory.close();                              		// chiudo la Factory di EM - a conclusione dei testcase per il DAO
    }

    protected abstract void init() throws IllegalAccessException;


}
