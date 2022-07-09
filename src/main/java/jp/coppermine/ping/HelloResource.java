package jp.coppermine.ping;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Sample JAX-RS resources.
 *
 */
@Path("hello")
@RequestScoped
public class HelloResource {
    
    @GET
    public String getMessage() {
        return "Hello, world";
    }
    
}
