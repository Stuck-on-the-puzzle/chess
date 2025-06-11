package dataaccess;

import chess.ChessGame;
import model.GameData;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;

public class MySQLGameDAO extends BaseDAO implements GameDao {

    private final Gson serializer = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS game(
            `gameID` INT NOT NULL,
            `whiteUsername` varchar(255),
            `blackUsername` varchar(255),
            `gameName`  varchar(255),
            `chessGame` TEXT,
            PRIMARY KEY (`gameID`)
            )
            """
        };
        configureDatabase(createStatements);
    }
    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var s = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
            try (var statement = conn.prepareStatement(s)){
                var chessGameJson = serializer.toJson(gameData.game());
                statement.setInt(1, gameData.gameID());
                statement.setString(2, gameData.whiteUsername());
                statement.setString(3, gameData.blackUsername());
                statement.setString(4, gameData.gameName());
                statement.setString(5, chessGameJson);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?")){
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    if (!results.next()) {
                        throw new DataAccessException("Game with given gameID not found");
                    }
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
        GameData game = getGame(gameID);
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Spot Already Taken");
            }
            updatePlayerColor(gameID, "whiteUsername", username);
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Spot Already Taken");
            }
            updatePlayerColor(gameID, "blackUsername", username);
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
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE from game")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing game data");
        }
    }

    @Override
    public void updatePlayerColor(Integer gameID, String playerColor, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET " + playerColor + "=? WHERE gameID=?")){
                if (username == null) {
                    statement.setNull(1, Types.NULL);
                }
                else {
                    statement.setString(1, username);
                }
                statement.setInt(2, gameID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error Updating Player Color");
        }
    }

    @Override
    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE game SET chessGame=? WHERE gameID=?")){
                String chessGameJson = serializer.toJson(game);
                statement.setString(1,chessGameJson);
                statement.setInt(2, gameID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating Game");
        }
    }
}
