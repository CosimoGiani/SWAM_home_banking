package rest.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import model.User;
import otp.OneTimePasswordAuthenticator;
import otp.UserResource;

/**
 * Register JAX-RS resources.
 *
 */
@ApplicationPath("api")
@ApplicationScoped
public class RestApplicationConfig extends Application {
	
	/*
	@GET
    @Path("/sayHello")
    public String getHelloMsg() {
        return "Hello World";
    }
	*/
		
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	private HashMap<String, String> userSecretMap = new HashMap<String, String>();
		
	// Per l'inizializzazione
	public RestApplicationConfig() {
		classes.add(RestApplicationConfig.class);
		classes.add(RegistrationService.class);
		classes.add(UserResource.class);
		classes.add(HelloResource.class);
		classes.add(AuthenticationService.class);
	    //HashMap<String, String> userSecretMap = new HashMap<String, String>();
	    this.userSecretMap.put("user1@example.com", "pass1");
	    singletons.add(new OneTimePasswordAuthenticator(this.userSecretMap));
	}
	
	// Per quando si vuole aggiornare la mappa, cioè quando un utente si vuole autenticare
	public RestApplicationConfig(User user) {
		updateMap(user);
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
	@Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
	
	/*
	@POST
	@Path("/provaPost/{name}")
	public String updateUserData(@PathParam("name") String name) {
		System.out.println(name);
		return name;
	}
	*/
		
	public void updateMap(User user) {
		this.userSecretMap.put(user.getEmail(), user.getPassword());
		this.singletons.add(new OneTimePasswordAuthenticator(this.userSecretMap));
	}	
	
	
}
