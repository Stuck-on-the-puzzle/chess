package dataaccess;

import model.GameData;

import java.util.HashSet; // use HashSet to keep them all unique!!!

public interface GameDao {
    // implement interfaces with database that have to do with the games

    // when accessing database, you should have an exception to throw
    void createGame(GameData gameData) throws DataAccessException; // adds GameData to the database
    GameData getGame(int gameID) throws DataAccessException; // gets GameData from the database
    // if there is an issue getting the GameData from the database, and exception is thrown.
    HashSet<GameData> listGames(); // gets a hash set of GameData from the database
    // if there is an issue getting the GameData from the database, and exception is thrown.
    void joinGame(int gameID, String playerColor, String username) throws DataAccessException;
    boolean usedGameID(int gameID);
    void clear() throws DataAccessException;
}
