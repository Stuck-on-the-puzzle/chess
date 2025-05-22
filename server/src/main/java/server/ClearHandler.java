package server;

import service.ClearService;
import service.RequestResult.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import spark.Route;

public class ClearHandler implements Route{

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request req, Response res) {

      clearResult clearMes = clearService.clear();
      return gson.toJson(clearMes);
    }
}
