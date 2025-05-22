package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.RequestResult.loginRequest;
import service.RequestResult.loginResult;
import service.RequestResult.registerResult;

public class UserServiceTest {

    static UserDAO userDAO;
    static AuthDAO authDAO;
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
        userDAO.clear();
        authDAO.clear();
        user = new UserData("username", "password", "email@email.com");
    }

    @Test
    @DisplayName("Register Test Positive")
    public void registerUserPos() throws DataAccessException {
        registerResult result = userService.register(user);
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
        registerResult result = userService.register(user);
        loginRequest loginRequest = new loginRequest(user.username(),user.password());
        loginResult loginResult = userService.login(loginRequest);
        AuthData authData = new AuthData(loginResult.authToken(), user.username());
        Assertions.assertEquals(authDAO.getAuth(loginResult.authToken()), authData);
    }

    @Test
    @DisplayName("Login Test Negative")
    public void loginUserNeg() throws DataAccessException {
        userService.register(user);
        loginRequest loginRequest = new loginRequest(user.username(),"notRightPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Test Positive")
    public void logoutUserPos() throws DataAccessException {
        userService.register(user);
        loginRequest loginRequest = new loginRequest(user.username(),user.password());
        loginResult loginResult = userService.login(loginRequest);
        userService.logout(loginResult.authToken());
        // check that the authToken from login is no longer found
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("Logout Test Negative")
    public void logoutUserNeg() throws DataAccessException {
        // check that unauthorized token causes an error
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("randomAuth"));
    }
}
