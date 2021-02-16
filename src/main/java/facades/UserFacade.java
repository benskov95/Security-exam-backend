package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

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
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        addInitialRoles(em);
        checkRole(user, em);
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

        Query query = em.createQuery("SELECT u FROM User u WHERE u.username =:username ");
        query.setParameter("username", user.getUsername());

       List<User> result = query.getResultList();
        if(result.size() > 0){
            throw new AuthenticationException("A user with this username already exists!");
        }
    }

    public void checkRole(User user, EntityManager em){
        String param;
        if (user.getUsername().equals("admin")) {
            param = "admin";
        } else {
            param = "user";
        }
        Query query = em.createQuery("SELECT r FROM Role r WHERE r.roleName =:role ");
        query.setParameter("role", param);
        user.addRole((Role) query.getSingleResult());
    }
    
    public void addInitialRoles(EntityManager em) {
        Query query = em.createQuery("SELECT r FROM Role r");
        if (query.getResultList().isEmpty()) {
            em.getTransaction().begin();
            em.persist(new Role("user"));
            em.persist(new Role("admin"));
            em.getTransaction().commit();
        }
    }
    
}
