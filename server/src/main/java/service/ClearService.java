package service;

import dataaccess.*;
import service.RequestResult.ClearResult;

public class ClearService {

    // implements the main functions of the program (one the seven functions)
    // this class implements the clear function

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public ClearResult clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        return new ClearResult("Clear Successful");
    }
}
