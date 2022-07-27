package session;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionTransferManager {
	
	private HashMap<String, HashMap<String, String>> sessionCodeHolder;
	private Random rand;
	private Timer t;
	
	public SessionTransferManager(){
		sessionCodeHolder = new HashMap<>();
		rand = new Random();
		t = new Timer();
	}
	
	public String requestSessionTransfer(String email, String otp) {
		HashMap<String, String> sessionCredentials = new HashMap<>();
		sessionCredentials.put("email", email);
		sessionCredentials.put("otp", otp);
		
		String sessionCode = generateSessionCode();
		
		sessionCodeHolder.put(sessionCode, sessionCredentials);
		
		SessionTransferTimer timer = new SessionTransferTimer(this, sessionCode);
		t.schedule(timer, 0);
		
		return sessionCode;
	}
	
	public void removeSessionTransfer(String sessionCode) {
		HashMap<String, String> outcome = sessionCodeHolder.remove(sessionCode);
		if(outcome != null) {
			// se non è null allora abbiamo rimosso le credenziali perchè è trascorso un minuto
			System.out.println("Timeout Trasferimento di Sessione - Codice Sessione non più valido");
		}
		// altrimenti le credenziali sono state già rimosse perchè il trasferimento sessione è già avvenuto con successo
	}
	
	public HashMap<String, String> getSessionCredentials(String sessionCode){
		HashMap<String, String> sessionCredentials = sessionCodeHolder.get(sessionCode);
		if(sessionCredentials != null) {
			sessionCodeHolder.remove(sessionCode);
			System.out.println("Codice Sessione utilizzato con successo");
		}
		return sessionCredentials;
	}
	
	private String generateSessionCode() {
		int randomCode = rand.nextInt(10000000, 99999999); 
		return String.valueOf(randomCode);
	}
}
