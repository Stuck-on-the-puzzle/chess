package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.RequestResult.loginRequest;
import service.RequestResult.loginResult;
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
    public Object handle(Request req, Response res) {
        loginRequest loginRequest = gson.fromJson(req.body(), loginRequest.class);
        loginResult loginResult;
        try {
            res.status(200);
            loginResult = userService.login(loginRequest);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Incorrect Password") || e.getMessage().equals("User Does Not Exist")) {
                res.status(401);
            } else if (e.getMessage().equals("Missing Username or Password")) {
                res.status(400);
            }
            loginResult = new loginResult("Error Logging in");
        }
        return gson.toJson(loginResult);
    }
}
