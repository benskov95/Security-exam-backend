package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JOSEException;
import dto.PostDTO;
import errorhandling.NotFound;
import facades.PostFacade;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;
import security.JWTAuthenticationFilter;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;
import security.UserPrincipal;


@Path("post")
public class PostResource {


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JWTAuthenticationFilter jwt = new JWTAuthenticationFilter();
    public static final PostFacade POST_FACADE = PostFacade.getPostFacade(EMF);


    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getPostsByThreadId(@PathParam("id") int id) throws NotFound {
        List<PostDTO> postDTOs = POST_FACADE.getPostsByThreadId(id);
        return GSON.toJson(postDTOs);
    }
    
    @POST
    @RolesAllowed({"user", "admin", "moderator"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addPost(String post, @HeaderParam("x-access-token") String token) throws ParseException, JOSEException, AuthenticationException, NotFound {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        PostDTO postDTO = GSON.fromJson(post, PostDTO.class);
        PostDTO newPost = POST_FACADE.addPost(postDTO, user.getEmail());
        return GSON.toJson(newPost);
    }
    
    @DELETE
    @RolesAllowed({"user", "admin", "moderator"})
    @Path("me/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteMyPost(@PathParam("id") int id, @HeaderParam("x-access-token") String token) throws AuthenticationException, ParseException, JOSEException, NotFound {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        PostDTO deletedPost = POST_FACADE.deleteMyPost(id, user.getEmail());
        return GSON.toJson(deletedPost);
    }
    
    @DELETE
    @RolesAllowed({"admin", "moderator"})
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePost(@PathParam("id") int id) throws NotFound {
        PostDTO deletedPost = POST_FACADE.deletePost(id);
        return GSON.toJson(deletedPost);
    }
    
    @PUT
    @RolesAllowed({"user", "admin", "moderator"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String editPost(String post, @HeaderParam("x-access-token") String token) throws NotFound, ParseException, JOSEException, AuthenticationException {
        UserPrincipal user = jwt.getUserPrincipalFromTokenIfValid(token);
        PostDTO postDTO = GSON.fromJson(post, PostDTO.class);
        PostDTO editedDTO = POST_FACADE.editMyPost(postDTO, user.getEmail());
        return GSON.toJson(editedDTO);
    }
}
