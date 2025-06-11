package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDao {
    // use to store game data in a list or map for phase 3

    private final HashSet<GameData> gameDatadb;

    public MemoryGameDAO() {
        this.gameDatadb = new HashSet<>();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        for (GameData game : gameDatadb) {
            if (game.equals(gameData)) {
                throw new DataAccessException("Game Already Exists");
            }
        }
        gameDatadb.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game: gameDatadb) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Cannot get GameData");
    }

    @Override
    public HashSet<GameData> listGames() {
        return gameDatadb;
    }

    @Override
    public void joinGame(int gameID, String playerColor, String username) throws DataAccessException{
        for (GameData game : gameDatadb) {
            if (game.gameID() == gameID) {
                if (playerColor.equals("WHITE")) {
                    if (game.whiteUsername() != null) {
                        throw new DataAccessException("Spot Already Taken");
                    } else {
                        gameDatadb.remove(game);
                        GameData joinedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                        gameDatadb.add(joinedGame);
                        return;
                    }
                } else {
                    if (game.blackUsername() != null) {
                        throw new DataAccessException("Spot Already Taken");
                    } else {
                        gameDatadb.remove(game);
                        GameData joinedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                        gameDatadb.add(joinedGame);
                        return;
                    }
                }
            }
        }
        throw new DataAccessException("No Game matches ID");
    }

    public boolean usedGameID(int gameID) {
        for (GameData game : gameDatadb) {
            if (game.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        gameDatadb.clear();
    }

    @Override
    public void updatePlayerColor(Integer gameID, String playerColor, String username) throws DataAccessException {
        for (GameData game : gameDatadb) {
            if (game.gameID() == gameID) {
                gameDatadb.remove(game);
                GameData updatedGame;
                if ("WHITE".equalsIgnoreCase(playerColor)) {
                    updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                } else if ("BLACK".equalsIgnoreCase(playerColor)) {
                    updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                } else {
                    throw new DataAccessException("Invalid color");
                }
                gameDatadb.add(updatedGame);
                return;
            }
        }
        throw new DataAccessException("Game ID not found");
    }

    @Override
    public void updateGame(Integer gameID, ChessGame chessGame) throws DataAccessException {
        for (GameData game : gameDatadb) {
            if (game.gameID() == gameID) {
                gameDatadb.remove(game);
                GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
                gameDatadb.add(updatedGame);
                return;
            }
        }
        throw new DataAccessException("Game ID not found");
    }
}
