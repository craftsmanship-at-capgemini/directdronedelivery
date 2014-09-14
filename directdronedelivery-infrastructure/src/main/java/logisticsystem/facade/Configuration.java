package logisticsystem.facade;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

public class Configuration {
    @Qualifier
    @Retention(RUNTIME)
    @Target({ TYPE, METHOD, FIELD, PARAMETER })
    @Documented
    public @interface Config {
        Param value();
    }
    
    public enum Param {
        BASEURI
    };
    
    @Produces
    @Config(Param.BASEURI)
    public String baseUri() {
        return "http://localhost:8080/logisticsystem/rest";
    }
    
}
