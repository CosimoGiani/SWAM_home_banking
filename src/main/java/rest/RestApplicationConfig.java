package rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

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
	
}
