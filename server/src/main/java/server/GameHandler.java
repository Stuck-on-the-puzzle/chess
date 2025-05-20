package server;

import dataaccess.DataAccessException;
import service.GameService;
import service.RequestResult.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import spark.Route;

public abstract class GameHandler implements Route {

    GameService gameService;

    public Object createHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        CreateRequest createRequest = gson.fromJson(req.body(), CreateRequest.class);
        return gson.toJson(gameService.createGame(createRequest));

    }

    public Object joinHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);
        return gson.toJson(gameService.joinGame(joinRequest));
    }

    public Object listHandler(Request req, Response res)  throws DataAccessException {
        Gson gson = new Gson();
        ListRequest listRequest = gson.fromJson(req.body(), ListRequest.class);
        return gson.toJson(gameService.listGames(listRequest));
    }

}
