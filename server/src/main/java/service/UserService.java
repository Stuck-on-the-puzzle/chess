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
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(UserData registerRequest) throws DataAccessException {
        // verify input
        String username = registerRequest.username();
        try { // Check requested username. If not already taken, make new user
            userDAO.createUser(registerRequest);
        } catch (DataAccessException e) {
            throw new DataAccessException("Username already taken");
        }
        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));
        // The user should be logged in now

        return new RegisterResult(username, authToken, "Register User Successful");
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        String username = loginRequest.username();
        String password = loginRequest.password();
        // check if user exists. If it does, check if password matches
        // if credentials are all good, create authToken and proceed
        try {
            userDAO.checkCredentials(username, password);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error with Login");
        }
        String authToken = generateToken();
        // user should be logged in now
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException{
        String authToken = logoutRequest.authToken();
        isAuthenticated(authToken);
        authDAO.deleteAuth(authToken);
        // user should be logged out now

        return new LogoutResult("Logout Successful");
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
