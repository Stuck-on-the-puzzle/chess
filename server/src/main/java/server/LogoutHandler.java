package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import service.RequestResult.LoginRequest;
import service.RequestResult.LogoutRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res)  throws DataAccessException {
        LogoutRequest logoutRequest = gson.fromJson(req.body(), LogoutRequest.class);
        return gson.toJson(userService.logout(logoutRequest));
    }
}
