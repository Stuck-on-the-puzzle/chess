package service;
import dataaccess.*;

public class BaseClass {

    AuthDAO authDAO;

    public void isAuthenticated(String authToken) throws DataAccessException{
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized");
        }
    }
}
