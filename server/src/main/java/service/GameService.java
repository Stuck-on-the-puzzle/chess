package service;

import dataaccess.*;
import model.GameData;
import service.requestresult.*;

import java.util.concurrent.ThreadLocalRandom;

public class GameService extends BaseClass {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the createGame, joinGame, listGames functions

    private final GameDao gameDAO;

    public GameService(GameDao gameDAO, AuthDao authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public CreateResult createGame(CreateRequest create, String authToken) throws DataAccessException {
        try {
            isAuthenticated(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        if (create.gameName() == null) {
            throw new DataAccessException("Bad Request");
        }
        int gameID;
        do { // generates a non used gameID
            gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
        } while (gameDAO.usedGameID(gameID));

        try {
            gameDAO.createGame(new GameData(gameID, null, null, create.gameName(), null));
        } catch (DataAccessException e) {
            throw new DataAccessException("Bad Request");
        }

        return new CreateResult(gameID);
    }

    public JoinResult joinGame(JoinRequest join, String authToken) throws DataAccessException {
        isAuthenticated(authToken);
        Integer gameID = join.gameID();
        String color = join.playerColor(); // will be WHITE or BLACK
        if (color == null || (!color.equals("BLACK") && !color.equals("WHITE")) || gameID == null) {
            throw new DataAccessException("Error with player color");
        }
        String username = authDAO.getAuth(authToken).username();
        gameDAO.joinGame(gameID, color, username);

        return new JoinResult("Game Joined Successfully");
    }

    public ListResult listGames(String authToken) throws DataAccessException{
        isAuthenticated(authToken);
        return new ListResult(gameDAO.listGames());
    }
}
