package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import service.RequestResult.*;

import java.util.ArrayList;

public class GameService extends BaseClass {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the createGame, joinGame, listGames functions

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public CreateResult createGame(CreateRequest create) throws DataAccessException {
        isAuthenticated(create.authToken());
        int gameID = create.gameName();
        try {
            gameDAO.createGame(new GameData(gameID, null, null, null, null));
        } catch (DataAccessException e) {
            throw new DataAccessException("Game Already Exists");
        }
        
        return new CreateResult(gameID, "Game Created Successfully");
    }

    public JoinResult joinGame(JoinRequest join) throws DataAccessException {
        return new JoinResult("Game Joined Successfully");
    }

    public ListResult listGames(ListRequest list) throws DataAccessException{

        ArrayList<String> games = new ArrayList<>();
        return new ListResult(games, "Listed Games Successfully");
    }
}
