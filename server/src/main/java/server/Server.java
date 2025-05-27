package server;

import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private static final UserDao userDAO;
    private static final AuthDao authDAO;
    private static final GameDao gameDAO;

    static {
        try {
            userDAO = new MySQLUserDAO();
            gameDAO = new MySQLGameDAO();
            authDAO = new MySQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(userService);
        createHandler = new CreateHandler(gameService);
        joinHandler = new JoinHandler(gameService);
        listHandler = new ListHandler(gameService);
        clearHandler = new ClearHandler(clearService);

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
