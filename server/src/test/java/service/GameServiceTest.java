package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import service.requestresult.*;

public class GameServiceTest {

    static UserDao userDAO;
    static AuthDao authDAO;
    static GameDao gameDAO;
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
    @DisplayName("Create Test Positive")
    public void createGamePos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        String authToken = result.authToken();
        CreateRequest create = new CreateRequest("GameName");
        CreateResult createResult = gameService.createGame(create, authToken);
        // make sure the game you made can be found in database
        Assertions.assertEquals(createResult.gameID(), gameDAO.getGame(createResult.gameID()).gameID());
    }

    @Test
    @DisplayName("Create Test Negative")
    public void createGameNeg() throws DataAccessException {
        CreateRequest create = new CreateRequest("GameName");
        // check that unauthorized token won't allow game creation
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(create, "badAuthToken"));
    }

    @Test
    @DisplayName("Join Test Positive")
    public void registerUserPos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        String authToken = result.authToken();
        CreateRequest create = new CreateRequest("GameName");
        CreateResult createResult = gameService.createGame(create, authToken);
        int gameID = createResult.gameID();
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID);
        gameService.joinGame(joinRequest, authToken);
        // make sure the username in the white color matches user username
        Assertions.assertEquals(user.username(), gameDAO.getGame(createResult.gameID()).whiteUsername());
    }

    @Test
    @DisplayName("Join Test Negative")
    public void registerUserNeg() throws DataAccessException {
        RegisterResult result = userService.register(user);
        UserData secondUser = new UserData("second", "pass" , "e");
        RegisterResult result2 = userService.register(secondUser);
        String authToken = result.authToken();
        String authToken2 = result2.authToken();
        CreateRequest create = new CreateRequest("GameName");
        CreateResult createResult = gameService.createGame(create, authToken);
        int gameID = createResult.gameID();
        JoinRequest joinRequest = new JoinRequest("WHITE", gameID);
        gameService.joinGame(joinRequest, authToken);
        JoinRequest joinRequest2 = new JoinRequest("WHITE", gameID);
        // make sure error throws when player tries to be the same color
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(joinRequest2, authToken2));
    }

    @Test
    @DisplayName("List Test Positive")
    public void loginUserPos() throws DataAccessException {
        RegisterResult result = userService.register(user);
        String authToken = result.authToken();
        CreateRequest create = new CreateRequest("GameName");
        CreateRequest create2 = new CreateRequest("GameName");
        CreateRequest create3 = new CreateRequest("GameName");
        gameService.createGame(create, authToken);
        gameService.createGame(create2, authToken);
        gameService.createGame(create3, authToken);
        ListResult listResult = gameService.listGames(authToken);
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
