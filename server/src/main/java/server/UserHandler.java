package server;

import dataaccess.DataAccessException;
import service.RequestResult.*;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import model.UserData;
import spark.Route;

public abstract class UserHandler implements Route {

    UserService userService;

    public Object registerHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        UserData userData = gson.fromJson(req.body(), UserData.class);
        return gson.toJson(userService.register(userData));

    }

    public Object loginHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        return gson.toJson(userService.login(loginRequest));
    }

    public Object logoutHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        LogoutRequest logoutRequest = gson.fromJson(req.body(), LogoutRequest.class);
        return gson.toJson(userService.logout(logoutRequest));
    }

}
