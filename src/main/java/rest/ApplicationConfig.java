package rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("jersey.config.server.wadl.disableWadl", "true");
        return properties;
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(cors.CorsFilter.class);
        resources.add(errorhandling.NotFoundMapper.class);
        resources.add(errorhandling.AlreadyExistsMapper.class);
        resources.add(errorhandling.GenericExceptionMapper.class);
        resources.add(errorhandling.InputNotValidMapper.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(rest.CategoryResource.class);
        resources.add(rest.PostResource.class);
        resources.add(rest.UserResource.class);
        resources.add(rest.ThreadResource.class);
        resources.add(security.JWTAuthenticationFilter.class);
        resources.add(security.LoginEndpoint.class);
        resources.add(security.AuthEndpoint.class);
        resources.add(security.RolesAllowedFilter.class);
        resources.add(security.errorhandling.AuthenticationExceptionMapper.class);
        resources.add(security.errorhandling.NotAuthorizedExceptionMapper.class);
        resources.add(logs.RequestLoggingFilter.class);

        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
//    private void addRestResourceClasses(Set<Class<?>> resources) {
//        resources.add(cors.CorsFilter.class);
//        resources.add(errorhandling.AlreadyExistsMapper.class);
//        resources.add(errorhandling.GenericExceptionMapper.class);
//        resources.add(errorhandling.InputNotValidMapper.class);
//        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
//        resources.add(rest.CategoryResource.class);
//        resources.add(rest.PostResource.class);
//        resources.add(rest.UserResource.class);
//        resources.add(security.JWTAuthenticationFilter.class);
//        resources.add(security.LoginEndpoint.class);
//        resources.add(security.RolesAllowedFilter.class);
//        resources.add(security.errorhandling.AuthenticationExceptionMapper.class);
//        resources.add(security.errorhandling.NotAuthorizedExceptionMapper.class);
//
//    }

}
