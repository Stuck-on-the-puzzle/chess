package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO extends BaseDAO implements AuthDao {

    public MySQLAuthDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth(
            `authToken` varchar(255) NOT NULL,
            `username` varchar(255) NOT NULL,
            PRIMARY KEY (`authToken`)
            )
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException{
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeVoidUpdate(statement, authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        getAuth(authToken);
        executeVoidUpdate("DELETE FROM auth WHERE authToken = ?", authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try {
            return executeQuery(statement, rs -> safeMap(rs, r -> {
                if (r.next()) {
                    return new AuthData(r.getString("authToken"), r.getString("username"));
                } else {
                    throw new DataAccessException("Unauthorized");
                }
            }), authToken);
        } catch (RuntimeException e) {
            // Unwrap DataAccessException and rethrow it
            if (e.getCause() instanceof DataAccessException) {
                throw (DataAccessException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    @Override
    public void clear() throws DataAccessException{
        executeVoidUpdate("DELETE FROM auth");
    }
}
