package otp;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@OTPAuthenticated
@Priority(Priorities.AUTHENTICATION)
@Provider
public class OneTimePasswordAuthenticator implements ContainerRequestFilter {
	
	protected Map<String, String> userSecretMap;
	
	public OneTimePasswordAuthenticator() {}
	
	public OneTimePasswordAuthenticator(Map<String, String> userSecretMap) {
		this.userSecretMap = userSecretMap;
		// Qui dovrebbe recuperare la entry/riga relativa ad un utente specifico
		Map.Entry<String,String> entry = userSecretMap.entrySet().iterator().next();
		//
		String segreto_otp = OTP.generateToken(entry.getValue());
		//String segreto_otp = OTP.generateToken(userSecretMap.get(email));
		System.out.println("==============================================================");
		System.out.println("Il codice OTP è: " + segreto_otp);
		System.out.println("ed è stato inviato alla seguente mail: " + entry.getKey());
		System.out.println("==============================================================");
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		System.out.println("La mappa è: " + userSecretMap);
		
		// Recuperiamo l'autorizzazione della richiesta
		String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		System.out.println("Autorizzazione è: " + authorization);
		
	    if (authorization == null) {
	    	throw new NotAuthorizedException("Autorizzazione nulla");
	    }
	    
	    // Estraiamo dall'authorization header email e otp
	    String[] split = authorization.split(" ");
	    final String user = split[0];
	    String otp = split[1];
	    System.out.println("Autorizzazione user è: " + user + " e otp è: " + otp);
	    
	    // Estraiamo la password dalla mappa ...
	    String secret = userSecretMap.get(user);
	    System.out.println("Password nella mappa é: " + secret);
	    if (secret == null)
	    	throw new NotAuthorizedException("Password nella mappa inesistente");
	    
	    // ... e controlliamo che l'otp generato dalla password della mappa corrisponda all'otp presente nell'autorizzazione
	    String regen = OTP.generateToken(secret);
	    System.out.println("Regen è: " + regen);
	    if (!regen.equals(otp)) {
	    	throw new NotAuthorizedException("Regen e OTP non coincidenti");
	    }
	    
	    final SecurityContext securityContext = requestContext.getSecurityContext();
	    requestContext.setSecurityContext(new SecurityContext() {
	    	
	    	@Override
	        public Principal getUserPrincipal() {
	    		return new Principal() {
	    			@Override
	    			public String getName() {
	    				return user;
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
		
	}

}
