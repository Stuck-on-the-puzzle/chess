package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {

    // implements the main functions of the program (one the seven functions)
    // this class implements the clear function

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
