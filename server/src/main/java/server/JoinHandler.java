package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.RequestResult.JoinRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinHandler implements Route {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res)  throws DataAccessException {
        JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);
        return gson.toJson(gameService.joinGame(joinRequest));
    }
}
