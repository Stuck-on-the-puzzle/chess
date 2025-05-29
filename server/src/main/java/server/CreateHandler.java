package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.requestresult.CreateRequest;
import service.requestresult.CreateResult;
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
        CreateRequest createRequest = gson.fromJson(req.body(), CreateRequest.class);
        String authToken = req.headers("authorization");
        CreateResult createResult;
        try {
            createResult = gameService.createGame(createRequest, authToken);
            res.status(200);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Unauthorized")) {
                res.status(401);
            }
            else if (e.getMessage().equals("Bad Request")) {
                res.status(400);
            }
            else {
                res.status(500);
            }
            createResult = new CreateResult("Error Creating Game");
        }
        return gson.toJson(createResult);
    }
}
