package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDao {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException{
        String authToken = UUID.randomUUID().toString();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")){
                statement.setString(1, authToken);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DataAccessException("Auth already taken");
            }
            else {
                throw new DataAccessException("Database Error");
            }
        }
        return authToken;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                getAuth(authToken);
                statement.setString(1, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database Error");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")){
                statement.setString(1, authToken);
                try (var results = statement.executeQuery()) {
                    if (!results.next()) {
                        throw new DataAccessException("Unauthorized");
                    }
                    var username = results.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database Error");
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE from auth")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth data");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth(
            `authToken` varchar(255) NOT NULL,
            `username` varchar(255) NOT NULL,
            PRIMARY KEY (`authToken`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to Configure Database");
        }
    }
}
