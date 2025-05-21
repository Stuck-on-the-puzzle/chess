package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.RequestResult.LoginRequest;
import service.RequestResult.LoginResult;
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
        LoginResult loginResult;
        try {
            res.status(200);
            loginResult = userService.login(loginRequest);
        } catch (DataAccessException e) {
            res.status(401);
            loginResult = new LoginResult("Error Logging in");
        }
        return gson.toJson(loginResult);
    }
}
