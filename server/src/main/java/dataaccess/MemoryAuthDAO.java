package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDao {
    // use to store authentication data in a list or map for phase 3

    private final Map<String, AuthData> authDatadb;

    public MemoryAuthDAO() {
        this.authDatadb = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        authDatadb.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDatadb.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = authDatadb.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        return auth;
    }

    @Override
    public void clear() {
        authDatadb.clear();
    }
}
