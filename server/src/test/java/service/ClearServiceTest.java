package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import service.requestresult.RegisterResult;

public class ClearServiceTest extends GameClearSetup {

    static UserService userService;
    static ClearService clearService;


    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
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
