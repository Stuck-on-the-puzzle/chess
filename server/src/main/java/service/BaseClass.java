package service;
import dataaccess.*;

public class BaseClass {

    protected final AuthDao authDAO;

    public BaseClass(AuthDao authDAO) {
        this.authDAO = authDAO;
    }

    public void isAuthenticated(String authToken) throws DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Unauthorized");
        }
    }
}
