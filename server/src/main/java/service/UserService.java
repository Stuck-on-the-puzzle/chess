package service;

import dataaccess.*;
import model.UserData;
import service.requestresult.*;

import java.util.UUID;

public class UserService {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the register, login, logout functions

    private final UserDao userDAO;
    private final AuthDao authDAO;

    public UserService(UserDao userDAO, AuthDao authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(UserData registerRequest) throws DataAccessException {
        // verify input
        String username = registerRequest.username();
        String password = registerRequest.password();
        if (username == null || password == null) {
            throw new DataAccessException("Bad Request");
        }
        userDAO.createUser(registerRequest);
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);
        // The user should be logged in now
        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        String username = loginRequest.username();
        String password = loginRequest.password();
        // check if user exists. If it does, check if password matches
        // if credentials are all good, create authToken and proceed
        if (username == null || password == null) {
            throw new DataAccessException("Bad Request");
        }
        userDAO.checkCredentials(username, password);
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);
        // user should be logged in now
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) throws DataAccessException{
        authDAO.deleteAuth(authToken);
        // user should be logged out now
        return new LogoutResult("Logout Successful");
    }

}
