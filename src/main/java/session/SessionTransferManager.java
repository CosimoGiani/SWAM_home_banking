package session;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import otpStateful.OneTimePasswordAuthenticator;

@ApplicationScoped
public class SessionTransferManager {
	
	@Inject
	private OneTimePasswordAuthenticator oTPAuthenticator;
	
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
			String newOtp = oTPAuthenticator.regenerateOTP(sessionCredentials.get("email"));
			sessionCredentials.put("otp", newOtp);
			
			sessionCodeHolder.remove(sessionCode);
			System.out.println("Codice Sessione utilizzato con successo");
		}
		return sessionCredentials;
	}
	
	private String generateSessionCode() {
		int randomCode = rand.nextInt(89999999) + 10000000; 
		return String.valueOf(randomCode);
	}
}
