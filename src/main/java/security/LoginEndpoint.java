package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entities.Role;
import facades.UserFacade;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.persistence.EntityManagerFactory;
import utils.CaptchaSecret;
import utils.EMF_Creator;

@Path("login")
public class LoginEndpoint {

  public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
  private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
  public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(String jsonString) throws AuthenticationException, Exception {
    JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
    String email = json.get("email").getAsString();
    String password = json.get("password").getAsString();
    String captchaToken;

    try {
        captchaToken = json.get("captchaToken").getAsString();
    } catch(Exception e) {
        throw new AuthenticationException("Captcha verification failed. Please try again.");
    }

    if (captchaToken != null && !verifyCaptcha(captchaToken)) {
        throw new AuthenticationException("Captcha verification failed. Please try again.");
    }

    try {
      User user = USER_FACADE.getVerifiedUser(email, password);
      Boolean checkAdmin = USER_FACADE.authAdmin(user.getEmail());

      String token = createToken(user, user.getRole());
      JsonObject responseJson = new JsonObject();

      if(checkAdmin){
        responseJson.addProperty("user_id", user.getEmail());
      }else{
        responseJson.addProperty("token", token);
      }

      return Response.ok(new Gson().toJson(responseJson)).build();

    } catch (JOSEException | AuthenticationException ex) {
      if (ex instanceof AuthenticationException) {
        throw (AuthenticationException) ex;
      }
      Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
    }
     throw new AuthenticationException("Invalid email or password! Please try again");
  }

  private String createToken(User user, Role role) throws JOSEException {

    String issuer = "BornIT";

    JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
    Date date = new Date();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .claim("email", user.getEmail())
            .claim("username", user.getUsername())
            .claim("role", role.getRoleName())
            .claim("imageUrl", user.getImageUrl())
            .claim("issuer", issuer)
            .issueTime(date)
            .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
            .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(signer);
    return signedJWT.serialize();

  }

    private boolean verifyCaptcha(String captchaToken) throws MalformedURLException, IOException, ParseException {
      if (captchaToken.length() > 1) {
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + CaptchaSecret.SECRET_KEY + "&response=" + captchaToken;
        URL myUrl = new URL(url);

        HttpsURLConnection con = (HttpsURLConnection) myUrl.openConnection();
        con.setRequestMethod("POST");
        con.setFixedLengthStreamingMode(0);
        con.setRequestProperty("Content-Type", "*");
        con.setDoOutput(true);
        con.setDoInput(true);

        JSONParser jsonParser = new JSONParser();
        JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(con.getInputStream(), "UTF-8"));
        return (boolean) response.get("success");
      } else {
          return false;
      }
  }

}
