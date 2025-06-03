package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.LogoutResult;
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
            if (e.getMessage().equals("Unauthorized")) {
                res.status(401);
            }
            else if (e.getMessage().equals("Bad Request")) {
                res.status(400);
            }
            else {
                res.status(500);
            }
            logoutResult = new LogoutResult("Error Logging Out");
        }
        return gson.toJson(logoutResult);
    }
}
