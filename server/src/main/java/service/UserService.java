package service;

import dataaccess.*;
import model.UserData;
import service.RequestResult.*;

public class UserService {

    // implements the main functions of the program (three of the seven functions)
    // this class implements the register, login, logout functions

    public RegisterResult register(RegisterRequest registerRequest) {
        // verify input
        // validate passed in authToken
        // Check requested username is not already taken
        // Create new User Mode object: User u = new User(...)
        // Insert new user into database by calling UserDao.createUser(u)
        // Log in user and create new AuthToken model object.
        // insert authToken into database
        // return user Data

        return null;
    }

    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}
