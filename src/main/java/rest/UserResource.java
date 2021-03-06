package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JOSEException;
import dto.UserDTO;
import errorhandling.InputNotValid;
import errorhandling.NotFound;
import facades.UserFacade;
import logs.Logged;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;
import security.JWTAuthenticationFilter;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;


@Path("user")
public class UserResource {


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JWTAuthenticationFilter jwt = new JWTAuthenticationFilter();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);


    @GET
    @RolesAllowed("user")
    @Path("count")
    @Produces({MediaType.APPLICATION_JSON})
    public String getNumberOfUsers() {
        int numberOfUsers = USER_FACADE.getAllUsers().size();
        return "{\"count\":" + numberOfUsers + "}";
    }
    @GET
    @Path("me")
    @RolesAllowed({"user", "admin", "moderator"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getUser(@HeaderParam("x-access-token") String token) throws ParseException, JOSEException, AuthenticationException, NotFound {

        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        UserDTO userDTO = USER_FACADE.getUser(user.getEmail());
        return GSON.toJson(userDTO);
    }

    @PUT
    @Path("me")
    @RolesAllowed({"user", "admin", "moderator"})
    @Logged
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String editUser(@HeaderParam("x-access-token") String token, String user) throws ParseException, JOSEException, AuthenticationException, NotFound, InputNotValid {

        UserPrincipal userPrincipal = jwt.getUserPrincipalFromTokenIfValid(token);
        UserDTO userDTO = GSON.fromJson(user, UserDTO.class);
        userDTO.setEmail(userPrincipal.getEmail());
        UserDTO editedUser = USER_FACADE.editUser(userDTO);

        return GSON.toJson(editedUser);
    }


    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Logged
    public String addUser(String user) throws InputNotValid {
        UserDTO userDTO = GSON.fromJson(user, UserDTO.class);
        UserDTO newUser = USER_FACADE.addUser(userDTO);
        return GSON.toJson(newUser);
    }

    @GET
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public String getUsers() {
        List<UserDTO> dtoList = USER_FACADE.getAllUsers();
        return GSON.toJson(dtoList);

    }

    @DELETE
    @Path("{email}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    @Logged
    public String deleteUser(@PathParam("email") String email) {
        UserDTO userDTO = USER_FACADE.deleteUser(email);
        return GSON.toJson(userDTO);
    }

    @PUT
    @RolesAllowed("admin")
    @Path("/promote/{email}")
    @Produces({MediaType.APPLICATION_JSON})
    @Logged
    public String promoteUser (@PathParam("email") String email) {

        UserDTO userDTO = USER_FACADE.promoteUser(email);

        return GSON.toJson(userDTO);
    }

    @PUT
    @RolesAllowed("admin")
    @Path("/demote/{email}")
    @Logged
    @Produces({MediaType.APPLICATION_JSON})
    public String demoteUser (@PathParam("email") String email) {

        UserDTO userDTO = USER_FACADE.demoteUser(email);

        return GSON.toJson(userDTO);
    }


}
