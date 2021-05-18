package logs;


import com.nimbusds.jose.JOSEException;
import errorhandling.GenericExceptionMapper;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;


@Logged
@Provider
public class RequestLoggingFilter implements ContainerResponseFilter {

    private static final JWTAuthenticationFilter jwt = new JWTAuthenticationFilter();

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {


        String token = containerRequestContext.getHeaderString("x-access-token");
        if (token != null){
        try {
           UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
           String info = "USER: " + user.getEmail()+ ", Host: "+ containerRequestContext.getHeaders().get("host") + ", METHOD: " + containerRequestContext.getMethod()
                    + ", URL: " + containerRequestContext.getUriInfo().getAbsolutePath() + ", Response code: " + containerResponseContext.getStatus();

           Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.INFO, info);



        } catch (ParseException | AuthenticationException | JOSEException e) {
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.INFO,e.getMessage());
        }
        }




        }
    }


