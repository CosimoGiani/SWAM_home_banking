package otpStateful;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Timer;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;


@OTPAuthenticatedStateful
@Priority(Priorities.AUTHENTICATION)
@Provider
@ApplicationScoped
public class OneTimePasswordAuthenticator implements ContainerRequestFilter {
	
	private HashMap<String, HashMap<String, String>> userOtpMap;
	/*
	 * {
	 *  email : {
	 *  		"otp": "aaaaaaaa",
	 *  		"timeToExpire": "12"        // in minutes
	 * 		}
	 * } 
	 */
	
	private String otpDefaultDuration;
	private Timer otpTimer;
	private HashMap<String, OTPTimerTask> otpTimerTasks;
	 
	
	public OneTimePasswordAuthenticator() {
		userOtpMap = new HashMap<String, HashMap<String, String>>();
		otpDefaultDuration = "5";    // minutes
		otpTimer = new Timer();
		otpTimerTasks = new HashMap<String, OTPTimerTask>();
	}
	
	public void generateOTP(String email) {
		String secret_otp = OTP.generateToken();
		HashMap<String, String> otpAndTime = new HashMap<String, String>();
		otpAndTime.put("otp", secret_otp);
		otpAndTime.put("timeToExpire", otpDefaultDuration);
		
		otpTimerTasks.put(email, new OTPTimerTask(this, email));
		otpTimer.scheduleAtFixedRate(otpTimerTasks.get(email),  1000 * 60, 1000 * 60);
		
		userOtpMap.put(email, otpAndTime);
		this.sendOTP(email);
	}
	
	public String regenerateOTP(String email) {
		String secret_otp = OTP.generateToken();
		if(refreshOTP(email)) {
			userOtpMap.get(email).put("otp", secret_otp);
		} else {
			HashMap<String, String> otpAndTime = new HashMap<String, String>();
			otpAndTime.put("otp", secret_otp);
			otpAndTime.put("timeToExpire", otpDefaultDuration);
			
			otpTimerTasks.put(email, new OTPTimerTask(this, email));
			otpTimer.scheduleAtFixedRate(otpTimerTasks.get(email),  1000 * 60, 1000 * 60);
			
			userOtpMap.put(email, otpAndTime);
		}
		return secret_otp;
	}
	
	public void removeOTP(String email) {
		userOtpMap.remove(email);
		otpTimerTasks.get(email).cancel();
		otpTimerTasks.remove(email);
		System.out.println("OTP legata alla email:" + email + " è scaduto.");
	}
	
	public void decreaseExpirationTime(String email) {
		if(this.userOtpMap.containsKey(email)) {
			int t = Integer.valueOf(this.userOtpMap.get(email).get("timeToExpire"));
			if(t <= 1)
				removeOTP(email);
			else
				this.userOtpMap.get(email).put("timeToExpire", String.valueOf(t - 1));
		}
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		System.out.println("La mappa è: " + userOtpMap);
		
		// Recuperiamo l'autorizzazione della richiesta
		String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		// System.out.println("Autorizzazione è: " + authorization);
		
	    if (authorization == null) {
	    	throw new NotAuthorizedException("Autorizzazione nulla");
	    }
	    
	    // Estraiamo dall'authorization header email e otp
	    String[] split = authorization.split(" ");
	    final String email = split[0];
	    String otp = split[1];
	    
	    if(!userOtpMap.containsKey(email))
	    	throw new NotAuthorizedException("Nessuna otp associata a questa mail");
	    
	    // Estraiamo l'otp generata dalla mappa ...
	    String secret_otp = userOtpMap.get(email).get("otp");
	    if (secret_otp == null)
	    	throw new NotAuthorizedException("Password nella mappa inesistente");
	    
	    // ... e controlliamo che tale otp corrisponda all'otp presente nell'autorizzazione
	    if (!secret_otp.equals(otp)) {
	    	throw new NotAuthorizedException("OTP memorizzata e OTP fornita non coincidenti");
	    }
	    
	    final SecurityContext securityContext = requestContext.getSecurityContext();
	    requestContext.setSecurityContext(new SecurityContext() {
	    	
	    	@Override
	        public Principal getUserPrincipal() {
	    		return new Principal() {
	    			@Override
	    			public String getName() {
	    				return email;
	    			}
	    		};
	    	}
	    	
	    	@Override
	        public boolean isUserInRole(String role) {
	           return false;
	        }
	    	
	    	@Override
	        public boolean isSecure() {
	           return securityContext.isSecure();
	        }

	    	@Override
	        public String getAuthenticationScheme() {
	           return "OTP";
	        }
	    	
	    });
	    
	    refreshOTP(email);
		
	}
	
	private boolean refreshOTP(String email) {
		if(this.userOtpMap.containsKey(email)) {
			this.userOtpMap.get(email).put("timeToExpire", otpDefaultDuration);
			return true;
		} else {
			return false;
		}
	}
	
	private void sendOTP(String email) {
		String secret_otp = userOtpMap.get(email).get("otp");
		
		System.out.println("==============================================================");
		System.out.println("Il codice OTP è: " + secret_otp);
		System.out.println("ed è stato inviato alla seguente mail: " + email);
		System.out.println("==============================================================");
		
	}

}
