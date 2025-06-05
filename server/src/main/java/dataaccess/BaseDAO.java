package dataaccess;

import exception.ResponseException;
import static java.sql.Types.NULL;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class BaseDAO {

    public void configureDatabase(String[] createStatements) throws DataAccessException {
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

    protected void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            if (param instanceof String p) {
                ps.setString(i + 1, p);
            } else if (param instanceof Integer p) {
                ps.setInt(i + 1, p);
            } else if (param instanceof Enum<?> p) {
                ps.setString(i + 1, p.name());
            } else if (param == null) {
                ps.setNull(i + 1, NULL);
            } else {
                throw new SQLException("Unsupported parameter type");
            }
        }
    }

    protected int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Unable to update database");
        }
    }

    protected void executeVoidUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Database update failed" );
        }
    }

    protected <T> T executeQuery(String sql, Function<ResultSet, T> mapper, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (var rs = ps.executeQuery()) {
                return mapper.apply(rs);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Query failed");
        }
    }

    protected <T> T safeMap(ResultSet rs, ResultSetMapper<T> mapper) {
        try {
            return mapper.map(rs);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException, DataAccessException;
    }
}
