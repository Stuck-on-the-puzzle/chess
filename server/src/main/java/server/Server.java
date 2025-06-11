package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import requestresult.ClearResult;
import org.eclipse.jetty.websocket.api.Session;
import spark.*;

import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final UserDao USER_DAO;
    private static final AuthDao AUTH_DAO;
    private static final GameDao GAME_DAO;

    static {
        try {
            USER_DAO = new MySQLUserDAO();
            GAME_DAO = new MySQLGameDAO();
            AUTH_DAO = new MySQLAuthDAO();
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

    static ConcurrentHashMap<Session, Integer> gameSessions = new ConcurrentHashMap<>();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserService userService = new UserService(USER_DAO, AUTH_DAO);
        GameService gameService = new GameService(GAME_DAO, AUTH_DAO);
        ClearService clearService = new ClearService(USER_DAO, AUTH_DAO, GAME_DAO);

        registerHandler = new RegisterHandler(userService);
        loginHandler = new LoginHandler(userService);
        logoutHandler = new LogoutHandler(userService);
        createHandler = new CreateHandler(gameService);
        joinHandler = new JoinHandler(gameService);
        listHandler = new ListHandler(gameService);
        clearHandler = new ClearHandler(clearService);

        Spark.webSocket("/ws", WebsocketHandler.class);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler); // clear
        Spark.post("/user", registerHandler); // register
        Spark.post("/session", loginHandler); // login
        Spark.delete("/session", logoutHandler); // logout
        Spark.get("/game", listHandler); // list games
        Spark.post("/game", createHandler); // create game
        Spark.put("/game", joinHandler); // join game

        // throw error for database error
        Spark.exception(DataAccessException.class, (error, req, res) -> {
            res.status(500); // Internal Server Error
            ClearResult clearResult = new ClearResult("Error with Database/Server");
            Gson gson = new Gson();
            res.body(gson.toJson(clearResult));
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static UserDao getUserDao() {
        return USER_DAO;
    }

    public static GameDao getGameDao() {
        return GAME_DAO;
    }

    public static AuthDao getAuthDao() {
        return AUTH_DAO;
    }
}
