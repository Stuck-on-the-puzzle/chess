package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestResult.*;

import java.util.UUID;

public class UserService extends BaseClass {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the register, login, logout functions

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
    }

    public RegisterResult register(UserData registerRequest) throws DataAccessException {
        // verify input
        String username = registerRequest.username();
        String password = registerRequest.password();
        if (username == null || password == null) {
            throw new DataAccessException("Missing Username or Password");
        }
        try { // Check requested username. If not already taken, make new user
            userDAO.createUser(registerRequest);
        } catch (DataAccessException e) {
            throw new DataAccessException("Username already taken");
        }
        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));
        // The user should be logged in now

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        String username = loginRequest.username();
        String password = loginRequest.password();
        // check if user exists. If it does, check if password matches
        // if credentials are all good, create authToken and proceed
        if (username == null || password == null) {
            throw new DataAccessException("Missing Username or Password");
        }
        userDAO.checkCredentials(username, password);
        String authToken = generateToken();
        // user should be logged in now
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) throws DataAccessException{
        isAuthenticated(authToken);
        authDAO.deleteAuth(authToken);
        // user should be logged out now

        return new LogoutResult("Logout Successful");
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
