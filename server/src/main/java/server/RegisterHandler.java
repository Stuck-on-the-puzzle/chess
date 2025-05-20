package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res)  throws DataAccessException {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        return gson.toJson(userService.register(userData));
    }
}
