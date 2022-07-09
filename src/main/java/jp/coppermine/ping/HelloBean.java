package jp.coppermine.ping;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Sample Backing Bean for JavaServer Faces.
 *
 */
@Named
@RequestScoped
public class HelloBean {

    public String getMessage() {
        return "Hello, world";
    }
    
}
