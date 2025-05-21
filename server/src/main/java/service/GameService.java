package service;

import dataaccess.*;
import model.GameData;
import service.RequestResult.*;

public class GameService extends BaseClass {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the createGame, joinGame, listGames functions

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

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
        int gameID = join.gameID();
        String color = join.playerColor(); // will be WHITE or BLACK
        String username = authDAO.getAuth(join.authToken()).username();
        isAuthenticated(join.authToken());
        gameDAO.joinGame(gameID, color, username);

        return new JoinResult("Game Joined Successfully");
    }

    public ListResult listGames(ListRequest list) throws DataAccessException{
        isAuthenticated(list.authToken());
        return new ListResult(gameDAO.listGames(), "Listed Games Successfully");
    }
}
