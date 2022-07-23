package rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;

/**
 * Sample JAX-RS resources.
 *
 */

@Path("hello")
@RequestScoped
public class HelloResource {	
	
	@GET
	@Path("ciao")
    public String getMessage() {
        return "Hello, world";
    }
	
	@POST
	@Produces("application/json; charset=UTF-8")
	@Path("/provaPost/{name}")
	public String updateUserData(@PathParam("name") String name) {
		System.out.println(name);
		return name;
	}
	
	
    
}
