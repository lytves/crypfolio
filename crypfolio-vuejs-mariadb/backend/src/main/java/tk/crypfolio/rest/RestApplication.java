package tk.crypfolio.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    public RestApplication() {
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<Object>();
    }
}