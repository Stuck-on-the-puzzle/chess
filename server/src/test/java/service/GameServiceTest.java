package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import service.RequestResult.*;

public class GameServiceTest {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;
    static UserService userService;
    static GameService gameService;

    static UserData user;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @BeforeEach
    public void setup() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        user = new UserData("username", "password", "email@email.com");
    }

    @Test
    @DisplayName("Create Test Positive")
    public void createGamePos() throws DataAccessException {
        registerResult result = userService.register(user);
        String authToken = result.authToken();
        createRequest create = new createRequest("GameName");
        createResult createResult = gameService.createGame(create, authToken);
        // make sure the game you made can be found in database
        Assertions.assertEquals(createResult.gameID(), gameDAO.getGame(createResult.gameID()).gameID());
    }

    @Test
    @DisplayName("Create Test Negative")
    public void createGameNeg() throws DataAccessException {
        createRequest create = new createRequest("GameName");
        // check that unauthorized token won't allow game creation
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(create, "badAuthToken"));
    }

    @Test
    @DisplayName("Join Test Positive")
    public void registerUserPos() throws DataAccessException {
        registerResult result = userService.register(user);
        String authToken = result.authToken();
        createRequest create = new createRequest("GameName");
        createResult createResult = gameService.createGame(create, authToken);
        int gameID = createResult.gameID();
        joinRequest joinRequest = new joinRequest("WHITE", gameID);
        gameService.joinGame(joinRequest, authToken);
        // make sure the username in the white color matches user username
        Assertions.assertEquals(user.username(), gameDAO.getGame(createResult.gameID()).whiteUsername());
    }

    @Test
    @DisplayName("Join Test Negative")
    public void registerUserNeg() throws DataAccessException {
        registerResult result = userService.register(user);
        UserData secondUser = new UserData("second", "pass" , "e");
        registerResult result2 = userService.register(secondUser);
        String authToken = result.authToken();
        String authToken2 = result2.authToken();
        createRequest create = new createRequest("GameName");
        createResult createResult = gameService.createGame(create, authToken);
        int gameID = createResult.gameID();
        joinRequest joinRequest = new joinRequest("WHITE", gameID);
        gameService.joinGame(joinRequest, authToken);
        joinRequest joinRequest2 = new joinRequest("WHITE", gameID);
        // make sure error throws when player tries to be the same color
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(joinRequest2, authToken2));
    }

    @Test
    @DisplayName("List Test Positive")
    public void loginUserPos() throws DataAccessException {
        registerResult result = userService.register(user);
        String authToken = result.authToken();
        createRequest create = new createRequest("GameName");
        createRequest create2 = new createRequest("GameName");
        createRequest create3 = new createRequest("GameName");
        gameService.createGame(create, authToken);
        gameService.createGame(create2, authToken);
        gameService.createGame(create3, authToken);
        listResult listResult = gameService.listGames(authToken);
        // make sure the size of the list matches the amount of games added
        Assertions.assertEquals(3, listResult.games().size());
    }

    @Test
    @DisplayName("List Test Negative")
    public void loginUserNeg() {
        // make sure you can't list games without an authToken
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames("badAuthToken"));
    }
}
