package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;

public class MySQLAuthDAOTests {

    static UserDao userDAO;
    static AuthDao authDAO;
    static UserService userService;

    static UserData user;
    static AuthData auth;

    @BeforeAll
    public static void init() {
        try {
            userDAO = new MySQLUserDAO();
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        userService = new UserService(userDAO, authDAO);

    }

    @BeforeEach
    public void setup() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        DatabaseManager.createDatabase();

        user = new UserData("username", "password", "email@email.com");
        auth = new AuthData("auth", "username");
    }

    @Test
    @DisplayName("Create Auth Test Positive")
    public void createAuthPos() throws DataAccessException {
        authDAO.createAuth("auth", "username");
        // make sure the auth you made can be found in database
        Assertions.assertEquals("auth", authDAO.getAuth("auth").authToken());
    }

    @Test
    @DisplayName("Create Auth Test Negative")
    public void createAuthNeg() throws DataAccessException {
        authDAO.createAuth("auth", "username");
        // check that you can't use the same auth
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth("auth", "username"));
    }

    @Test
    @DisplayName("Delete Auth Test Positive")
    public void deletePos() throws DataAccessException {
        authDAO.createAuth("auth", "username");
        authDAO.deleteAuth("auth");
        // make sure the auth created is no longer there
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("auth"));
    }

    @Test
    @DisplayName("Delete Auth Test Negative")
    public void deleteNeg()  {
        // make sure error is thrown when you try to delete an auth that isn't there
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("badAuth"));
    }

    @Test
    @DisplayName("Get Auth Test Positive")
    public void getAuthPos() throws DataAccessException {
        authDAO.createAuth("goodAuth", "username");
        // make sure the created auth exists
        Assertions.assertEquals("goodAuth", authDAO.getAuth("goodAuth").authToken());

    }

    @Test
    @DisplayName("Get Auth Test Negative")
    public void getAuthNeg() throws DataAccessException {
        authDAO.createAuth("auth", "username");
        // make sure error is thrown if bad auth is given
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("badAuth"));
    }

    @Test
    @DisplayName("Clear Test")
    public void clear() throws DataAccessException {
        authDAO.createAuth("auth", "username");
        authDAO.createAuth("auth2", "username2");
        authDAO.createAuth("auth3", "username3");
        authDAO.createAuth("auth4", "username4");
        authDAO.clear();
        // make sure no auths after clear
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("auth"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("auth2"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("auth3"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("auth4"));
    }
}
