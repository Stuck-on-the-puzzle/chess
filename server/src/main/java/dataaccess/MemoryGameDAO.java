package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    // use to store game data in a list or map for phase 3

    HashSet<GameData> gameDatadb;

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        for (GameData game : gameDatadb) {
            if (game == gameData) {
                throw new DataAccessException("Game Already Exists");
            }
        }
        gameDatadb.add(gameData);
    }

    @Override
    public void updateGame(GameData gameData) {
        int gameID = gameData.gameID();
        for (GameData game: gameDatadb) {
            if (game.gameID() == gameID) {
                gameDatadb.remove(game);
                gameDatadb.add(gameData);
            }
        }
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
            if (playerColor.equals("WHITE")) {
                if (game.whiteUsername() == null) {
                    throw new DataAccessException("Spot Already Taken");
                }
                else {
                    gameDatadb.remove(game);
                    GameData joinedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                    gameDatadb.add(joinedGame);
                }
            }
            else {
                if (game.blackUsername() == null) {
                    throw new DataAccessException("Spot Already Taken");
                }
                else {
                    gameDatadb.remove(game);
                    GameData joinedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                    gameDatadb.add(joinedGame);
                }
            }
        }
    }

    @Override
    public void clear() {
        gameDatadb.clear();
    }
}
