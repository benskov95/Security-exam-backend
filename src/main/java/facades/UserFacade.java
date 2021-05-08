package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;


import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import errorhandling.InputNotValid;
import errorhandling.NotFound;
import security.errorhandling.AuthenticationException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public UserDTO deleteUser(String email) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, email);
            em.remove(user);
            em.getTransaction().commit();

            return new UserDTO(user);

        } finally {
            em.close();
        }
    }

    public UserDTO getUser(String email) throws NotFound {

        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, email);

        if (user == null) {
            throw new NotFound("No user found");
        }

        return new UserDTO(user);
    }


    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public List<UserDTO> getAllUsers() {

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery query = em.createQuery("SELECT u from User u", User.class);
            List<User> userList = query.getResultList();
            List<UserDTO> userDTOlist = new ArrayList<>();

            for (User user : userList) {
                userDTOlist.add(new UserDTO(user));
            }

            return userDTOlist;

        } finally {
            em.close();
        }

    }

    public UserDTO addUser(UserDTO userDTO) throws  InputNotValid {

        validateInput(userDTO);
        validatePw(userDTO.getPassword());

        EntityManager em = emf.createEntityManager();
        User user = new User(userDTO.getEmail(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getPhone());
        addInitialRoles(em);
        user.setRole(getUserRole(em));
        checkIfExists(user, em);
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);

        } finally {
            em.close();
        }
    }

    private void checkIfExists(User user, EntityManager em) throws InputNotValid {

        Query query = em.createQuery("SELECT u FROM User u WHERE u.username =:username OR u.email = :email");
        query.setParameter("username", user.getUsername()).setParameter("email", user.getEmail());

        List<User> result = query.getResultList();

        if (result.size() > 0) {
            if (result.get(0).getEmail().equals(user.getEmail())) {
                throw new InputNotValid("This email is already in use!");
            }
            if (result.get(0).getUsername().equals(user.getUsername())) {
                throw new InputNotValid("This username is already in use!");
            }
        }
    }

    private void checkIfUsernameExists(String username, EntityManager em) throws AuthenticationException {

        Query query = em.createQuery("SELECT u FROM User u where u.username = :username");
        query.setParameter("username", username);
        if (query.getResultList().size() > 0) {
            throw new AuthenticationException("This username is already in use!");
        }
    }


    public void addInitialRoles(EntityManager em) {
        Query query = em.createQuery("SELECT r FROM Role r");
        if (query.getResultList().isEmpty()) {
            em.getTransaction().begin();
            em.persist(new Role("user"));
            em.persist(new Role("admin"));
            em.persist(new Role("moderator"));
            em.getTransaction().commit();
        }
    }

    public Role getUserRole(EntityManager em) {
        return em.find(Role.class, "user");
    }

    public UserDTO editUser(UserDTO userDTO) throws AuthenticationException, NotFound, InputNotValid {

        validateInput(userDTO);

        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userDTO.getEmail());
        if (user == null) {
            throw new NotFound("User not found");
        }

        user.setUsername(userDTO.getUsername());

        if (!userDTO.getUsername().equals(user.getUsername())) {
            checkIfUsernameExists(user.getUsername(), em);
        }

        if (userDTO.getPassword() != null) {
            if (!userDTO.getPassword().equals("")) {
                validatePw(userDTO.getPassword());
                user.changePw(userDTO.getOldPassword(), userDTO.getPassword());
            }
        }
        
        user.setImageUrl(userDTO.getImageUrl());

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        } finally {
            em.close();

        }

    }

    public void validatePw(String pw) throws InputNotValid {

        Pattern patternPw = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$");
        Matcher matcherPw = patternPw.matcher(pw);
        boolean isPWOk = matcherPw.find();

        if (!isPWOk) {
            throw new InputNotValid("Your password must be minimum 8 characters, containing at least 1 uppercase and lowercase letter and at least 1 number.");
        }
    }


    public void validateInput(UserDTO userDTO) throws InputNotValid {

        Pattern patternEmail = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcherEmail = patternEmail.matcher(userDTO.getEmail());
        boolean isEmailOk = matcherEmail.find();


        Pattern patternUsername = Pattern.compile("^(?=[a-zA-Z0-9._]{4,16}$)(?!.*[_.]{2})[^_.].*[^_.]$");
        Matcher matcherUsername = patternUsername.matcher(userDTO.getUsername());
        boolean isUsernameOk = matcherUsername.find();

        if (!isEmailOk) {
            throw new InputNotValid("Invalid email!");
        }


        if (!isUsernameOk) {
            throw new InputNotValid("Your username must be between 4-16 characters long and consist only of alphanumeric characters " +
                    "( underscores and periods are allowed, but not at the start or the end of the username )");
        }

    }
    public Boolean authAdmin (String email) throws Exception{
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class,email);
        Role role = user.getRole();

        if (role.getRoleName().equals("admin")) {
            int randomPIN = new Random().nextInt(900000) + 100000;
            String generatedString = ""+randomPIN;
            user.setAuth(generatedString);
            System.out.println("User authcode = " + user.getAuth());

            int checksms = SendSMS(user.getPhone(), user.getAuth());
            System.out.println("SMS - Response " + checksms);
            try {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
                return true;
            } finally {
                em.close();
            }
        }else{
            return false;
        }
    }

    public int SendSMS (String phone, String auth) throws Exception{
        URL url = new URL("https://gatewayapi.com/rest/mtsms");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        try {
            wr.writeBytes(
                    "token=x7Ci3ErzQ0yh1nzr9MaRa8s8bFn-xXliNNTuHHu9Z-X9Mp_xhopM14zW7LIxVnOo"
                            + "&sender=" + URLEncoder.encode("BornIT", "UTF-8")
                            + "&message=" + URLEncoder.encode("Din kode " + auth, "UTF-8")
                            + "&recipients.0.msisdn=45"+phone
            );
            wr.close();
            int responseCode = con.getResponseCode();
            con.disconnect();
            return responseCode;

        } finally {
            wr.close();
            con.disconnect();
        }
    }

    public UserDTO promoteUser (String email){
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class,email);
        Role role = em.find(Role.class, "moderator");

        user.setRole(role);

        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        }finally {
            em.close();

        }
    }


    public UserDTO demoteUser (String email){
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class,email);
        Role role = em.find(Role.class, "user");

        user.setRole(role);

        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        }finally {
            em.close();

        }
    }
}
