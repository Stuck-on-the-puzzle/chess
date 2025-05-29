package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

public class MySQLGameDAOTests {

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
    @DisplayName("Create Test Positive")
    public void createGamePos() throws DataAccessException {
        gameDAO.createGame(game);
        // make sure the game you made can be found in database
        Assertions.assertEquals(game.game(), gameDAO.getGame(game.gameID()).game());
    }

    @Test
    @DisplayName("Create Test Negative")
    public void createGameNeg() throws DataAccessException {
        gameDAO.createGame(game);
        // check that you can't create the same game twice
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
    }

    @Test
    @DisplayName("Join Test Positive")
    public void JoinPos() throws DataAccessException {
        gameDAO.createGame(game);
        gameDAO.joinGame(1, "WHITE", "whiteUsername");
        // make sure the size of the list matches the amount of games added
        Assertions.assertEquals("whiteUsername", gameDAO.getGame(1).whiteUsername());
    }

    @Test
    @DisplayName("Join Test Negative")
    public void JoinNeg() throws DataAccessException {
        gameDAO.createGame(game);
        // make sure trying to take color that is already taken throws an error
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.joinGame(1, "BLACK", "username"));
    }

    @Test
    @DisplayName("List Test Positive")
    public void listPos() throws DataAccessException {
        ChessBoard board = new ChessBoard();
        ChessGame chessGame = new ChessGame();
        board.resetBoard();
        chessGame.setBoard(board);
        GameData game2 = new GameData(2, null, "blackUser", "defaultGame", chessGame);
        GameData game3 = new GameData(3, null, "blackUser", "defaultGame", chessGame);
        GameData game4 = new GameData(4, null, "blackUser", "defaultGame", chessGame);
        gameDAO.createGame(game);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);
        gameDAO.createGame(game4);
        // make sure the size of the list matches the amount of games added
        Assertions.assertEquals(4, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("List Test Negative")
    public void listNeg() throws DataAccessException {
        // make sure if no games are there, the list is empty
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("Get Game Positive")
    public void getPos() throws DataAccessException {
        gameDAO.createGame(game);
        // make sure if game created and retrieved match
        Assertions.assertEquals(game.gameID(), gameDAO.getGame(1).gameID());
    }

    @Test
    @DisplayName("Get Game Negative")
    public void getNeg() throws DataAccessException {
        // make sure error is thrown if you try to get a game that doesn't exist
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(5));
    }

    @Test
    @DisplayName("Used Game Positive")
    public void usedPos() throws DataAccessException {
        gameDAO.createGame(game);
        // make sure if usedGameID returns true for added game
        Assertions.assertTrue(gameDAO.usedGameID(game.gameID()));
    }

    @Test
    @DisplayName("Used Game Negative")
    public void usedNeg() throws DataAccessException {
        // make sure usedGameID returns false for not used gameID
        Assertions.assertFalse(gameDAO.usedGameID(game.gameID() + 1));
    }

    @Test
    @DisplayName("Clear Test")
    public void clear() throws DataAccessException {
        ChessBoard board = new ChessBoard();
        ChessGame chessGame = new ChessGame();
        board.resetBoard();
        chessGame.setBoard(board);
        GameData game2 = new GameData(2, null, "blackUser", "defaultGame", chessGame);
        GameData game3 = new GameData(3, null, "blackUser", "defaultGame", chessGame);
        GameData game4 = new GameData(4, null, "blackUser", "defaultGame", chessGame);
        gameDAO.createGame(game);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);
        gameDAO.createGame(game4);
        gameDAO.clear();
        // make sure no games after clear
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }
}
