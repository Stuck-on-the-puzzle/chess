package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.RequestResult.ListRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListHandler implements Route {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res)  throws DataAccessException {
        ListRequest listRequest = gson.fromJson(req.body(), ListRequest.class);
        return gson.toJson(gameService.listGames(listRequest));
    }
}
