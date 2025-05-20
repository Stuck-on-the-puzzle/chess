package service;
import dataaccess.*;

public class BaseClass {

    AuthDAO authDAO;

    public boolean isAuthenticated(String authToken) {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }
}
