package server;

import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    ClearHandler clearHandler;
    RegisterHandler registerHandler;
    LoginHandler loginHandler;
    LogoutHandler logoutHandler;
    CreateHandler createHandler;
    JoinHandler joinHandler;
    ListHandler listHandler;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        registerHandler = new RegisterHandler(new UserService());
        loginHandler = new LoginHandler(new UserService());
        logoutHandler = new LogoutHandler(new UserService());
        createHandler = new CreateHandler(new GameService());
        joinHandler = new JoinHandler(new GameService());
        listHandler = new ListHandler(new GameService());
        clearHandler = new ClearHandler(new ClearService());

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler); // clear
        Spark.post("/user", registerHandler); // register
        Spark.post("/session", loginHandler); // login
        Spark.delete("/session", logoutHandler); // logout
        Spark.get("/game", listHandler); // list games
        Spark.post("/game", createHandler); // create game
        Spark.put("/game", joinHandler); // join game
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
