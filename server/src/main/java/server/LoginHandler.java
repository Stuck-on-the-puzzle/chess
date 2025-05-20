package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import service.RequestResult.LoginRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res)  throws DataAccessException {
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        return gson.toJson(userService.login(loginRequest));
    }
}
