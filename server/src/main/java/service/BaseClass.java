package service;
import dataaccess.*;
import model.AuthData;

public class BaseClass {

    AuthDAO authDAO;

    public boolean isAuthenticated(String authToken ) throws DataAccessException {
        try {
            AuthData a = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }
}
