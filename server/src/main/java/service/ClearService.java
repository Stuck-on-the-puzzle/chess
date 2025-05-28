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

    public ClearResult clear() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            return new ClearResult("Error clearing data");
        }
        return new ClearResult("Clear Successful");
    }
}
