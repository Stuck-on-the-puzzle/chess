package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO extends BaseDAO implements UserDao {

    public MySQLUserDAO() throws DataAccessException {

        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user(
            `username` varchar(255) NOT NULL,
            `password` varchar(255) NOT NULL,
            `email` varchar(255) NOT NULL,
            PRIMARY KEY (`username`)
            )
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try  {
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            executeVoidUpdate(statement, userData.username(), hashUserPassword(userData.password()), userData.email());
        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLException se && se.getErrorCode() == 1062) {
                throw new DataAccessException("Username already taken");
            }
            else {
                throw e;
            }
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM user WHERE username=?";
        try {
            return executeQuery(statement, rs -> {
                try {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    } else {
                        throw new DataAccessException("User not found");
                    }
                } catch (SQLException | DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }, username);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof DataAccessException) {
                throw (DataAccessException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    @Override
    public void checkCredentials(String username, String password) throws DataAccessException {
        if (!verifyUser(username, password)) {
            throw new DataAccessException("Incorrect Username/Password");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        executeVoidUpdate("DELETE FROM user");
    }

    String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        var hashedPassword = readHashedPasswordFromDatabase(username);
        return hashedPassword != null && BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT password FROM user WHERE username = ?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        return results.getString("password");
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting password.");
        }
    }
}
