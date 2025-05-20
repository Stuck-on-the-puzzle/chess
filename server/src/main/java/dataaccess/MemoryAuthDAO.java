package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    // use to store authentication data in a list or map for phase 3

    private final HashSet<AuthData> authDatadb;

    public MemoryAuthDAO() {
        this.authDatadb = new HashSet<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        authDatadb.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDatadb.removeIf(auth -> auth.authToken().equals(authToken));
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth: authDatadb) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        throw new DataAccessException("Cannot get AuthData");
    }

    @Override
    public void clear() {
        authDatadb.clear();
    }
}
