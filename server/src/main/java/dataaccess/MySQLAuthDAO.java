package dataaccess;

import model.AuthData;

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
}
