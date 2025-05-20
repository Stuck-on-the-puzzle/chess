package server;

import spark.*;

public class Server {


    UserHandler userHandler;
    GameHandler gameHandler;
    ClearHandler clearHandler;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler); // clear
        Spark.post("/user", userHandler); // register
        Spark.post("/session", userHandler); // login
        Spark.delete("/session", userHandler); // logout
        Spark.get("/game", gameHandler); // list games
        Spark.post("/game", gameHandler); // create game
        Spark.put("/game", gameHandler); // join game
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
