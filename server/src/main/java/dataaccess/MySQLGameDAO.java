package dataaccess;

import chess.ChessGame;
import model.GameData;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDao {

    private final Gson serializer = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try {
            var chessGameJson = serializer.toJson(gameData.game());
            executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGameJson);
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to Create Game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")){
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    results.next();
                    var whiteUsername = results.getString("whiteUsername");
                    var blackUsername = results.getString("blackUsername");
                    var gameName = results.getString("gameName");
                    var chessGame = new ChessGame();
                    var chessGameJson = results.getString("chessGame");
                    chessGame = serializer.fromJson(chessGameJson, ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error Getting Game");
        }
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> gameData = new HashSet<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game")){
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        var gameID = results.getInt("gameID");
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = new ChessGame();
                        var chessGameJson = results.getString("chessGame");
                        chessGame = serializer.fromJson(chessGameJson, ChessGame.class);
                        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                        gameData.add(game);
                    }
                    return gameData;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error Getting Game");
        }
    }

    @Override
    public void joinGame(int gameID, String playerColor, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")){
                if (playerColor.equals("WHITE")) {
                    if (statement.executeQuery().next()) {
                        throw new DataAccessException("Color already chosen");
                    }
                    else {
                        GameData game = getGame(gameID);
                        try (var s = conn.prepareStatement("DELETE FROM game WHERE gameID=?")) {
                            s.setInt(1, gameID);
                            s.executeQuery();
                            GameData newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                            createGame(newGame);
                        } catch (SQLException e) {
                            throw new DataAccessException("GameDoes Not Exist");
                        }
                    }
                }

                else {
                    if (statement.executeQuery().next()) {
                        throw new DataAccessException("Color already chosen");
                    }
                    else {
                        GameData game = getGame(gameID);
                        try (var s = conn.prepareStatement("DELETE FROM game WHERE gameID=?")) {
                            s.setInt(1, gameID);
                            s.executeQuery();
                            GameData newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                            createGame(newGame);
                        } catch (SQLException e) {
                            throw new DataAccessException("GameDoes Not Exist");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error Getting Game");
        }
    }

    @Override
    public boolean usedGameID(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID FROM game WHERE gameID=?")){
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    return results.next();
                }
            }
        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";
        try {
            executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error clearing data");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game(
            `gameID` varchar(255) NOT NULL,
            `whiteUsername` varchar(255),
            `blackUsername` varchar(255),
            `gameName`  varchar(255),
            `chessGame` TEXT
            PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to Configure Database");
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData u) ps.setString(i + 1, u.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update Database");
        }
    }
}
