package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDao {
    // use to store authentication data in a list or map for phase 3

    private final Map<String, AuthData> authDatadb;

    public MemoryAuthDAO() {
        this.authDatadb = new HashMap<>();
    }

    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authDatadb.put(authData.authToken(), authData);
        return authToken;
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
