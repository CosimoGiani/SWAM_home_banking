package ConsultantAuthentication;

import java.util.TimerTask;

public class TokenTimerTask extends TimerTask {
	
	private ConsultantAuthenticator consultantAuthenticator;
	private String identificationNumber;
	
	public TokenTimerTask(ConsultantAuthenticator consultantAuthenticator, String identificationNumber) {
		super();
		this.consultantAuthenticator = consultantAuthenticator;
		this.identificationNumber = identificationNumber;
	}

	@Override
	public void run() {
		consultantAuthenticator.decreaseExpirationTime(identificationNumber);
		
	}

	
}
