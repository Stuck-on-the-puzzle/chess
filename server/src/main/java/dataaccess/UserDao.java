package dataaccess;

import model.UserData;

public interface UserDao {
    // implement interfaces with database that have to do with users

    void createUser(UserData userData) throws DataAccessException; // stores UserData in the database
    UserData getUser(String username) throws DataAccessException; // gets UserData from the database
    // if there is an issue getting the UserData from the database, and exception is thrown.
    void checkCredentials (String username, String password)  throws DataAccessException;
    void clear();
}
