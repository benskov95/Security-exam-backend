package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.NotFound;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("auth")
public class AuthEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(String jsonString) throws NotFound, JOSEException {
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        String authCode = json.get("auth").getAsString();
        String user_id = json.get("user_id").getAsString();
        
        // Cloudflare seems to add the name of a cookie to the user id, so it has to be removed
        // before the database is checked.
        if (user_id.contains(",")) {
            String[] parts = user_id.split(",");
            user_id = parts[0];
        }
        
        System.out.println("--------- NEW AUTH --------");
        System.out.println("User: "+ user_id);
        System.out.println("Authcode: "+ authCode);
        System.out.println("---------------------------");
        ////CHECK USER
        EntityManager em = EMF.createEntityManager();
        User user = em.find(User.class, user_id);

        JsonObject responseJson = new JsonObject();

        if (user == null){
            throw new NotFound("Could not find user.");
        }else{
            try {
                Query q = em.createQuery("SELECT u FROM User u WHERE u.auth = :auth");
                q.setParameter("auth", authCode);

                if(q.getSingleResult() != null){
                    String token = createToken(user, user.getRole());
                    responseJson.addProperty("token", token);
                }
            } catch (Exception e) {
                throw new NotFound("Auth code not valid. Please try to login again.");
            }

        }
        return Response.ok(new Gson().toJson(responseJson)).build();
    }

    private String createToken(User user, Role role) throws JOSEException {

        String issuer = "BornIT";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("role", role.getRoleName())
                .claim("phone", user.getPhone())
                .claim("imageUrl", user.getImageUrl())
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}