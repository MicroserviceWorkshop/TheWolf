package thewolf.sales;

import javax.inject.Named;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@Named
public class EndpointConfig extends ResourceConfig {

    public EndpointConfig() {
        this.register(SalesRestService.class);
        this.register(JacksonFeature.class);
    }

}
