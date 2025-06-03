package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import requestresult.RegisterResult;
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
    public Object handle(Request req, Response res) {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        RegisterResult registerResult;
        try {
            registerResult = userService.register(userData);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Username already taken")) {
                res.status(403);
            }
            else if (e.getMessage().equals("Bad Request")){
                res.status(400);
            }
            else {
                res.status(500);
            }
            System.out.println(res.status());
            registerResult = new RegisterResult("Error Registering");
        }
        return gson.toJson(registerResult);
    }
}
