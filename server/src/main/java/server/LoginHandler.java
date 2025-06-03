package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requestresult.LoginRequest;
import requestresult.LoginResult;
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
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult;
        try {
            res.status(200);
            loginResult = userService.login(loginRequest);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Incorrect Username/Password") || e.getMessage().equals("User Does Not Exist")) {
                res.status(401);
            } else if (e.getMessage().equals("Bad Request")) {
                res.status(400);
            }
            else {
                res.status(500);
            }
            loginResult = new LoginResult("Error Logging in");
        }
        return gson.toJson(loginResult);
    }
}
