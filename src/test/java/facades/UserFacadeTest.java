package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import org.junit.jupiter.api.*;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static User user, admin, both;


    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        user = new User("test@mail.dk","user", "test123");
        admin = new User("test@mail.dk","admin", "test123");
        both = new User("test@mail.dk","user_admin", "test123");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user.setRole(userRole);
            admin.setRole(adminRole);

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }


    @Test
    public void getAllUsersTest() {
        List<UserDTO> userDTOList = facade.getAllUsers();

        assertTrue(userDTOList.size() == 3);
    }

    @Test
    public void testDeleteUser() {
        String userName = user.getUsername();

        UserDTO userDTO = facade.deleteUser(userName);

        assertTrue(userDTO.getUsername().equals(userName));
    }

    @Test
    public void testAddUser() throws AuthenticationException {
        User newUser = new User("test@mail.dk","Test", "Testtest");
        UserDTO userDTO = facade.addUser(new UserDTO(newUser));

        List<UserDTO> userDTOList = facade.getAllUsers();

        assertTrue(userDTOList.size() == 4);

        assertTrue(userDTO.getUsername().equals(newUser.getUsername()));

    }

}

