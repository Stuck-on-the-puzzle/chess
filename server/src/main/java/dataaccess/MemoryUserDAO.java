package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    // use to store user data in a list or map for phase 3

    HashSet<UserData> userDatadb;

    @Override
    public void createUser(UserData userData) {
        userDatadb.add(userData);
    }

    @Override
    public void deleteUser(String username) {
        userDatadb.removeIf(user -> user.username().equals(username));
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user: userDatadb) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("Cannot get UserData");
    }

    @Override
    public void clear() {
        userDatadb.clear();
    }
}
