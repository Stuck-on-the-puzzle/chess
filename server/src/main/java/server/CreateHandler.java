package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.RequestResult.createRequest;
import service.RequestResult.createResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateHandler implements Route {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        createRequest createRequest = gson.fromJson(req.body(), createRequest.class);
        String authToken = req.headers("authorization");
        createResult createResult;
        try {
            createResult = gameService.createGame(createRequest, authToken);
            res.status(200);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Unauthorized")) {
                res.status(401);
            }
            else {
                res.status(400);
            }
            createResult = new createResult("Error Creating Game");
        }
        return gson.toJson(createResult);
    }
}
