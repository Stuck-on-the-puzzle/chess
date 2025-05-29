package service;

import dataaccess.*;
import model.GameData;
import service.requestresult.*;

import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the createGame, joinGame, listGames functions

    private final GameDao gameDAO;
    private final AuthDao authDAO;

    public GameService(GameDao gameDAO, AuthDao authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult createGame(CreateRequest create, String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);
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
        authDAO.getAuth(authToken);
        Integer gameID = join.gameID();
        String color = join.playerColor(); // will be WHITE or BLACK
        if (color == null || (!color.equals("BLACK") && !color.equals("WHITE")) || gameID == null) {
            throw new DataAccessException("Bad Request");
        }
        String username = authDAO.getAuth(authToken).username();
        gameDAO.joinGame(gameID, color, username);

        return new JoinResult("Game Joined Successfully");
    }

    public ListResult listGames(String authToken) throws DataAccessException{
        authDAO.getAuth(authToken);
        return new ListResult(gameDAO.listGames());
    }
}
