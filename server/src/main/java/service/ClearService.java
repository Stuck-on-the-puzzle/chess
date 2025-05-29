package service;

import dataaccess.*;
import service.requestresult.ClearResult;

public class ClearService {

    // implements the main functions of the program (one the seven functions)
    // this class implements the clear function

    private final UserDao userDAO;
    private final AuthDao authDAO;
    private final GameDao gameDAO;

    public ClearService(UserDao userDAO, AuthDao authDAO, GameDao gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new ClearResult("Clear Successful");
    }
}
