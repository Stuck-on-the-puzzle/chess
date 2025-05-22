package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.requestResult.ListResult;
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
        String authToken = req.headers("authorization");
        ListResult listResult;
        try {
            listResult = gameService.listGames(authToken);
            res.status(200);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Unauthorized")) {
                res.status(401);
            }
            else {
                res.status(400);
            }
            listResult = new ListResult("Error Listing Game");
        }
        return gson.toJson(listResult);
    }
}
