package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requestresult.LogoutResult;
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
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        LogoutResult logoutResult;
        try {
            logoutResult = userService.logout(authToken);
            res.status(200);
        } catch (DataAccessException e) {
            res.status(401);
            logoutResult = new LogoutResult("Error Logging Out");
        }
        return gson.toJson(logoutResult);
    }
}
