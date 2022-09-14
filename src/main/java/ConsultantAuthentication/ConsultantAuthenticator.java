package ConsultantAuthentication;

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


@AuthenticatedConsultant
@Priority(Priorities.AUTHENTICATION)
@Provider
@ApplicationScoped
public class ConsultantAuthenticator implements ContainerRequestFilter {
	
	private HashMap<String, HashMap<String, String>> tokenMap;
	/*
	 * {
	 *   identificationNumber : {  // matricola del consultant
	 *  		"token": "aaaaaaaa",
	 *  		"timeToExpire": "12"        // in minutes
	 * 		}
	 * } 
	 */
	
	private String tokenDefaultDuration;
	private Timer tokenTimer;
	private HashMap<String, TokenTimerTask> tokenTimerTasks;
	
	
	public ConsultantAuthenticator() {
		tokenMap = new HashMap<String, HashMap<String, String>>();
		tokenDefaultDuration = "5";    // minutes
		tokenTimer = new Timer();
		tokenTimerTasks = new HashMap<String, TokenTimerTask>();
	}
	
	public String generateToken(String identificationNumber) {
		String token = TokenGenerator.generateToken();
		
		HashMap<String, String> tokenAndTime = new HashMap<String, String>();
		tokenAndTime.put("token", token);
		tokenAndTime.put("timeToExpire", tokenDefaultDuration);
		
		if(!tokenTimerTasks.containsKey(identificationNumber)) {
			tokenTimerTasks.put(identificationNumber, new TokenTimerTask(this, identificationNumber));
			tokenTimer.scheduleAtFixedRate(tokenTimerTasks.get(identificationNumber), 1000 * 60, 1000 * 60);
		}
		
		tokenMap.put(identificationNumber, tokenAndTime);
		return token;
	}
	
	public void removeToken(String identificationNumber) {
		tokenMap.remove(identificationNumber);
		
		tokenTimerTasks.get(identificationNumber).cancel();
		tokenTimerTasks.remove(identificationNumber);
		
		System.out.println("Token per il Consultente " + identificationNumber + " è scaduto.");
	}
	
	public void decreaseExpirationTime(String identificationNumber) {
		if(this.tokenMap.containsKey(identificationNumber)) {
			
			int t = Integer.valueOf(this.tokenMap.get(identificationNumber).get("timeToExpire"));
			
			if(t <= 1)
				removeToken(identificationNumber);
			else
				this.tokenMap.get(identificationNumber).put("timeToExpire", String.valueOf(t - 1));	
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		System.out.println("La mappa è: " + tokenMap);
		
		// Recuperiamo l'autorizzazione della richiesta
		String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (authorization == null) {
	    	throw new NotAuthorizedException("Autorizzazione nulla");
	    }
		
		String[] split = authorization.split(" ");
	    final String identificationNumber = split[0];
	    String token = split[1];
	    
	    if(!tokenMap.containsKey(identificationNumber))
	    	throw new NotAuthorizedException("L'utente non è autenticato");
	    
	    String secret_token = tokenMap.get(identificationNumber).get("token");
	    if (secret_token == null)
	    	throw new NotAuthorizedException("L'utente non è autenticato");
	    
	    if (!secret_token.equals(token)) {
	    	throw new NotAuthorizedException("Token non valido");
	    }
	    
	    final SecurityContext securityContext = requestContext.getSecurityContext();
	    requestContext.setSecurityContext(new SecurityContext() {
	    	
	    	@Override
	        public Principal getUserPrincipal() {
	    		return new Principal() {
	    			@Override
	    			public String getName() {
	    				return identificationNumber;
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
	           return "Token";
	        }
	    	
	    });
	    
	    refreshToken(identificationNumber);
	    	
	}

	
	private boolean refreshToken(String identificationNumber) {
		if(this.tokenMap.containsKey(identificationNumber)) {
			this.tokenMap.get(identificationNumber).put("timeToExpire", tokenDefaultDuration);
			return true;	
		} else {
			return false;
		}
	}
	
}
