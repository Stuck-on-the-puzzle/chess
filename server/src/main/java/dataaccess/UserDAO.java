package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    // implement interfaces with database that have to do with users

    void createUser(UserData userData) throws DataAccessException; // stores UserData in the database
    void deleteUser(String username); // deletes UserData from the database - this will not be implemented based on lecture video
    UserData getUser(String username) throws DataAccessException; // gets UserData from the database
    // if there is an issue getting the UserData from the database, and exception is thrown.
    void checkCredentials (String username, String password)  throws DataAccessException;
    void clear();
}
