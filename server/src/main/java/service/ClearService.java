package service;

import dataaccess.*;
import service.requestResult.ClearResult;

public class ClearService {

    // implements the main functions of the program (one the seven functions)
    // this class implements the clear function

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        return new ClearResult("Clear Successful");
    }
}
