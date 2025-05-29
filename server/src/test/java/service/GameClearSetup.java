package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;

public abstract class GameClearSetup {

    protected static UserDao userDAO;
    protected static AuthDao authDAO;
    protected static GameDao gameDAO;

    protected static UserData user;

    @BeforeEach
    public void setup() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        user = new UserData("username", "password", "email@email.com");
    }
}
