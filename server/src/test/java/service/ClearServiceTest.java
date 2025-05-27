package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import service.requestresult.RegisterResult;

public class ClearServiceTest {

    static UserDao userDAO;
    static AuthDao authDAO;
    static GameDao gameDAO;
    static UserService userService;
    static ClearService clearService;

    static UserData user;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @BeforeEach
    public void setup() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        user = new UserData("username", "password", "email@email.com");
    }

    @Test
    @DisplayName("Clear Test Positive")
    public void clearPos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        clearService.clear();
        // make sure there are not authTokens or users in database
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(result.authToken()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(user.username()));
    }
}
