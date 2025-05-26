package dataaccess;

import model.AuthData;

public interface AuthDao {
    // implement interfaces with database that have to do with authentication

    void createAuth(AuthData authData) throws DataAccessException; // method to createAuth and put it in database
    void deleteAuth(String authToken); // method to delete authToken from database
    AuthData getAuth(String authToken) throws DataAccessException; // method to get authToken from the database
    // if there is an issue getting the AuthData from the database, and exception is thrown.
    void clear() throws DataAccessException;
}
