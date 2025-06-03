package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import requestresult.RegisterResult;

public class UserServiceTest {

    static UserDao userDAO;
    static AuthDao authDAO;
    static UserService userService;

    static UserData user;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @BeforeEach
    public void setup() {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        user = new UserData("username", "password", "email@email.com");
    }

    @Test
    @DisplayName("Register Test Positive")
    public void registerUserPos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        String authToken = result.authToken();
        AuthData authData = new AuthData(authToken, user.username());
        Assertions.assertEquals(authDAO.getAuth(authToken), authData);
    }

    @Test
    @DisplayName("Register Test Negative")
    public void registerUserNeg() {
        UserData badUser = new UserData(null, "password", "email@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(badUser));
    }

    @Test
    @DisplayName("Login Test Positive")
    public void loginUserPos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        LoginRequest loginRequest = new LoginRequest(user.username(),user.password());
        LoginResult loginResult = userService.login(loginRequest);
        AuthData authData = new AuthData(loginResult.authToken(), user.username());
        Assertions.assertEquals(authDAO.getAuth(loginResult.authToken()), authData);
    }

    @Test
    @DisplayName("Login Test Negative")
    public void loginUserNeg() throws DataAccessException {
        userService.register(user);
        LoginRequest loginRequest = new LoginRequest(user.username(),"notRightPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Test Positive")
    public void logoutUserPos() throws DataAccessException {
        userService.register(user);
        LoginRequest loginRequest = new LoginRequest(user.username(),user.password());
        LoginResult loginResult = userService.login(loginRequest);
        userService.logout(loginResult.authToken());
        // check that the authToken from login is no longer found
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("Logout Test Negative")
    public void logoutUserNeg() throws DataAccessException {
//        Assertions.assertNull(authDAO.find("randomAuth"));
//        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("randomAuth"));
        // check that unauthorized token causes an error
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("randomAuth"));
    }
}
