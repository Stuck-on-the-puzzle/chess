package server;

import dataaccess.DataAccessException;
import service.ClearService;
import service.RequestResult.*;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import model.UserData;
import spark.Route;

public abstract class ClearHandler implements Route{

    ClearService clearService;

    public void clearHandler(Request req, Response res)  throws DataAccessException {
//        Gson gson = new Gson();
        clearService.clear();
//        return gson.toJson();
    }
}
