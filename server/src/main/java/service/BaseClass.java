package service;
import dataaccess.*;

public class BaseClass {

    AuthDAO authDAO;

    public boolean isAuthenticated(String authToken) throws DataAccessException{
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized");
        }
        return true;
    }
}
