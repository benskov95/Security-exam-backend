package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JOSEException;
import dto.ThreadDTO;
import errorhandling.AlreadyExists;
import errorhandling.NotFound;
import facades.ThreadFacade;
import java.text.ParseException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import logs.Logged;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

@Path("thread")
public class ThreadResource {


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JWTAuthenticationFilter jwt = new JWTAuthenticationFilter();
    public static final ThreadFacade THREAD_FACADE = ThreadFacade.getThreadFacade(EMF);

    @GET
    @Path("{catId}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllThreadsByCatId(@PathParam("catId") int catId) throws NotFound {
        List<ThreadDTO> threadDTOs = THREAD_FACADE.getAllThreadsByCatId(catId);
        return GSON.toJson(threadDTOs);
    }

    @GET
    @Path("info/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getThreadById(@PathParam("id") int id) throws NotFound {
        ThreadDTO threadDTO = THREAD_FACADE.getThreadById(id);
        return GSON.toJson(threadDTO);
    }

    @POST
    @RolesAllowed({"user", "admin", "moderator"})
    @Logged
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addThread(String thread, @HeaderParam("x-access-token") String token) throws NotFound, AlreadyExists, ParseException, JOSEException, AuthenticationException {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        ThreadDTO threadDTO = GSON.fromJson(thread, ThreadDTO.class);
        ThreadDTO addedDTO = THREAD_FACADE.addThread(threadDTO, user.getEmail());
        return GSON.toJson(addedDTO);
    }

    @DELETE
    @RolesAllowed({"user", "admin", "moderator"})
    @Logged
    @Path("me/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteMyThread(@PathParam("id") int id, @HeaderParam("x-access-token") String token) throws ParseException, JOSEException, AuthenticationException, NotFound {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        ThreadDTO deletedDTO = THREAD_FACADE.deleteMyThread(id, user.getEmail());
        return GSON.toJson(deletedDTO);
    }

    @DELETE
    @RolesAllowed("admin")
    @Logged
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteThread(@PathParam("id") int id) throws NotFound {
        ThreadDTO deletedDTO = THREAD_FACADE.deleteThread(id);
        return GSON.toJson(deletedDTO);
    }

    @PUT
    @RolesAllowed({"user", "admin", "moderator"})
    @Logged
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String editMyThread(String thread, @HeaderParam("x-access-token") String token) throws NotFound, AuthenticationException, AlreadyExists, ParseException, JOSEException {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        ThreadDTO threadDTO = GSON.fromJson(thread, ThreadDTO.class);
        ThreadDTO editedDTO = THREAD_FACADE.editMyThread(threadDTO, user.getEmail());
        return GSON.toJson(editedDTO);
    }
}
