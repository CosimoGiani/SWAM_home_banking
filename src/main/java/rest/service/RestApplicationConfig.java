package rest.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

/**
 * Register JAX-RS resources.
 *
 */
@Path("/prova")
@ApplicationPath("api")
public class RestApplicationConfig extends Application {
	/*
	@GET
    @Path("/sayHello")
    public String getHelloMsg() {
        return "Hello World";
    }
	*/
	@POST
	//@Produces("application/json; charset=UTF-8")
	@Path("/provaPost/{name}")
	public String updateUserData(@PathParam("name") String name) {
		System.out.println(name);
		return name;
	}
	
	
}
