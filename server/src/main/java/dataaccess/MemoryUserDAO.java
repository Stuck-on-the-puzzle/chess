package dataaccess;

import model.UserData;
import service.BaseClass;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    // use to store user data in a list or map for phase 3

    private final HashSet<UserData> userDatadb;

    public MemoryUserDAO() {
        this.userDatadb = new HashSet<>();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        try {
            getUser(userData.username());
        } catch (DataAccessException e) {
            userDatadb.add(userData);
            return;
        }
        throw new DataAccessException("User already exists");
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

    public void checkCredentials (String username, String password) throws DataAccessException{
        for (UserData user: userDatadb) {
            if (user.username().equals(username)) {
                if (user.password().equals(password)) {
                    return;
                }
                else {
                    throw new DataAccessException("Incorrect Password");
                }
            }
        }
        throw new DataAccessException("User Does Not Exist");
    }

    @Override
    public void clear() {
        userDatadb.clear();
    }
}
