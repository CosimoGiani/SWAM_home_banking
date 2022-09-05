package otpStateful;

import java.util.TimerTask;

public class OTPTimerTask extends TimerTask {
	
	private OneTimePasswordAuthenticator otpAuthenticator;
	private String email;
	
	public OTPTimerTask(OneTimePasswordAuthenticator otpAuthenticator, String email) {
		super();
		this.otpAuthenticator = otpAuthenticator;
		this.email = email;
	}
	
	@Override
	public void run() {
		otpAuthenticator.decreaseExpirationTime(email);
	}

}
