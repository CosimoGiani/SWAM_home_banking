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
//@Provider
public class OneTimePasswordAuthenticator implements ContainerRequestFilter {
	
	protected Map<String, String> userSecretMap;
	
	public OneTimePasswordAuthenticator() {}
	
	public OneTimePasswordAuthenticator(Map<String, String> userSecretMap) {
		this.userSecretMap = userSecretMap;
		Map.Entry<String,String> entry = userSecretMap.entrySet().iterator().next();
		String segreto_otp = OTP.generateToken(entry.getValue());
		System.out.println("Segreto da mettere in header richiesta: " + segreto_otp);
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		// Recuperiamo l'autorizzazione della richiesta
		String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
	    if (authorization == null) {
	    	throw new NotAuthorizedException("Autorizzazione nulla");
	    }
	    
	    // Estraiamo dall'authorization header email e otp
	    String[] split = authorization.split(" ");
	    final String user = split[0];
	    String otp = split[1];
	    
	    // Visto che si passa l'email nella request recupero il corrispondente valore (cioè l'email dell'utente) ...
        MultivaluedMap<String, String> pathparam = requestContext.getUriInfo().getPathParameters();
        String userPathParam = pathparam.getFirst("email");
	    
	    // ... e controlliamo che l'utente della richesta sia lo stesso dell'autorizzazione
	    if (!userPathParam.equals(user)) {
	    	throw new NotAuthorizedException("Utente non autorizzato");
	    }
	    
	    // Estraiamo la password dalla mappa ...
	    String secret = userSecretMap.get(user);
	    if (secret == null)
	    	throw new NotAuthorizedException("Password nella mappa inesistente");
	    
	    // ... e controlliamo che l'otp generato dalla password della mappa corrisponda all'otp presente nell'autorizzazione
	    String regen = OTP.generateToken(secret);
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
