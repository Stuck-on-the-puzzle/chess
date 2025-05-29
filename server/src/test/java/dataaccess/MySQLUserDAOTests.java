package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

public class MySQLUserDAOTests {

    static UserDao userDAO;
    static AuthDao authDAO;
    static GameDao gameDAO;
    static UserService userService;
    static GameService gameService;

    static UserData user;
    static GameData game;

    @BeforeAll
    public static void init() {
        try {
            userDAO = new MySQLUserDAO();
            gameDAO = new MySQLGameDAO();
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        DatabaseManager.createDatabase();
        ChessBoard board = new ChessBoard();
        ChessGame chessGame = new ChessGame();
        board.resetBoard();
        chessGame.setBoard(board);

        game = new GameData(1, null, "blackUser", "defaultGame", chessGame);
        user = new UserData("username", "password", "email@email.com");
    }

    @Test
    @DisplayName("Create User Test Positive")
    public void createUserPos() throws DataAccessException {
        userDAO.createUser(user);
        // make sure the user you made can be found in database
        Assertions.assertEquals(userDAO.getUser(user.username()).username(), user.username());
    }

    @Test
    @DisplayName("Create User Test Negative")
    public void createUserNeg() throws DataAccessException {
        userDAO.createUser(user);
        // check that you can't use the same username
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    @DisplayName("Get User Test Positive")
    public void getUserPos() throws DataAccessException {
        userDAO.createUser(user);
        // make sure the size of the list matches the amount of games added
        Assertions.assertEquals(user.username(), userDAO.getUser(user.username()).username());
    }

    @Test
    @DisplayName("get User Test Negative")
    public void getUserNeg()  {
        // make sure error is thrown when you try to get a user that doesn't exist
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("username"));
    }

    @Test
    @DisplayName("Credentials Test Positive")
    public void credPos() {
        // make sure no error is thrown
        Assertions.assertDoesNotThrow(() -> {
            userDAO.createUser(user);
            userDAO.checkCredentials(user.username(), user.password());
        });
    }

    @Test
    @DisplayName("Credentials Test Negative")
    public void credNeg() throws DataAccessException {
        userDAO.createUser(user);
        // make sure error is thrown if wrong password is given
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.checkCredentials(user.username(), "wrongPassword"));
    }

    @Test
    @DisplayName("Clear Test")
    public void clear() throws DataAccessException {
        UserData user2 = new UserData("username2", "password2", "email2@email.com");
        UserData user3 = new UserData("username3", "password3", "email3@email.com");
        UserData user4 = new UserData("username4", "password4", "email4@email.com");
        userDAO.createUser(user);
        userDAO.createUser(user2);
        userDAO.createUser(user3);
        userDAO.createUser(user4);
        userDAO.clear();
        // make sure no users after clear
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(user.username()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(user2.username()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(user3.username()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(user4.username()));
    }
}
