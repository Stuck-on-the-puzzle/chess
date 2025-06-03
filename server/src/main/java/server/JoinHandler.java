package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import requestresult.JoinRequest;
import requestresult.JoinResult;
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
        String authToken = req.headers("authorization");
        JoinResult joinResult;
        try {
            joinResult = gameService.joinGame(joinRequest, authToken);
            res.status(200);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Spot Already Taken")) {
                res.status(403);
            }
            else if (e.getMessage().equals("Unauthorized")) {
                res.status(401);
            }
            else if (e.getMessage().equals("Bad Request")){
                res.status(400);
            }
            else {
                res.status(500);
            }
            joinResult = new JoinResult("Error Joining Game");
        }
        return gson.toJson(joinResult);
    }
}
