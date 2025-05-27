package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDao {

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, userData.username(), hashUserPassword(userData.password()), userData.email());
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to Create User. Name probably already exists.");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")){
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    results.next();
                    var password = results.getString("password");
                    var email = results.getString("email");
                    return new UserData(username, password, email);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public void checkCredentials(String username, String password) throws DataAccessException {
        if (!verifyUser(username, password)) {
            throw new DataAccessException("Wrong Username or Password");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM user";
        try {
            executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error clearing data");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user(
            `username` varchar(255) NOT NULL,
            `password` varchar(255) NOT NULL,
            `email` varchar(255) NOT NULL,
            PRIMARY KEY (`username`)
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData u) ps.setString(i + 1, u.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update Database");
        }
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
