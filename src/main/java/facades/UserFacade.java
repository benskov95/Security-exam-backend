package facades;

import com.nimbusds.jose.JOSEException;
import dto.UserDTO;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private static JWTAuthenticationFilter jwt;
    private UserFacade() {
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public UserDTO deleteUser(String userName) {

        EntityManager em = emf.createEntityManager();

        try{
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            em.remove(user);
            em.getTransaction().commit();

            return new UserDTO(user);

        }finally {
            em.close();
        }


    }
    public UserDTO getUser (String token) throws ParseException, JOSEException, AuthenticationException {
        UserPrincipal userFromToken = jwt.getUserPrincipalFromTokenIfValid(token);
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userFromToken.getEmail());


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

    public List<UserDTO> getAllUsers (){

        EntityManager em = emf.createEntityManager();

        try{
           TypedQuery query = em.createQuery("SELECT u from User u", User.class);
            List<User> userList = query.getResultList();
            List<UserDTO> userDTOlist = new ArrayList<>();

            for (User user: userList){
                userDTOlist.add(new UserDTO(user));
            }

            return userDTOlist;

        }finally {
            em.close();
        }

    }

    public UserDTO addUser (UserDTO userDTO) throws  AuthenticationException {

        EntityManager em = emf.createEntityManager();
        User user = new User(userDTO.getEmail(), userDTO.getUsername(), userDTO.getPassword());
        addInitialRoles(em);
        user.setRole(getUserRole(em));
        checkIfExists(user, em);
        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);

        }finally {
            em.close();
        }
    }

    private void checkIfExists(User user, EntityManager em) throws AuthenticationException {

        Query query = em.createQuery("SELECT u FROM User u WHERE u.username =:username OR u.email = :email");
        query.setParameter("username", user.getUsername()).setParameter("email", user.getEmail());

       List<User> result = query.getResultList();

        if(result.size() > 0){
            if(result.get(0).getEmail().equals(user.getEmail())) {
                throw new AuthenticationException("This email is already in use!");
            }
            if( result.get(0).getUsername().equals(user.getUsername())) {
                throw new AuthenticationException("This username is already in use!");
            }
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
    public Role getUserRole (EntityManager em){
        return em.find(Role.class,"user");
    }

}
