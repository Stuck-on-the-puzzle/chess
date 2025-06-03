package server;

import dataaccess.DataAccessException;
import requestresult.ClearResult;
import service.ClearService;
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
    public Object handle(Request req, Response res) throws DataAccessException {
      ClearResult clearMes = clearService.clear();
      res.status(200);
      return gson.toJson(clearMes);
    }
}
