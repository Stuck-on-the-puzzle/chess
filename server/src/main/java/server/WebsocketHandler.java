package server;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import exception.ResponseException;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.UserService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

    private static final Gson gson = new Gson();
    private final UserDao userDao = Server.getUserDao();
    private final GameDao gameDao = Server.getGameDao();
    private final AuthDao authDao = Server.getAuthDao();

    // Keep track of active sessions and user/game mapping
    private static final Map<Session, Integer> sessionGameMap = new ConcurrentHashMap<>();
    private static final Map<Session, String> sessionAuthMap = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress());
        Server.gameSessions.put((spark.Session) session, 0);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, DataAccessException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            String username = String.valueOf(this.userDao.getUser(command.getAuthToken()));

            sessionGameMap.put(session, command.getGameID());
            sessionAuthMap.put(session, command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, username, (Connect) command);
                case MAKE_MOVE -> handleMakeMove(session, username, (MakeMove) command);
                case LEAVE -> handleLeave(session, username, (Leave) command);
                case RESIGN -> handleResign(session, username, (Resign) command);
                default -> sendMessage(session, new ErrorMessage("Unknown command type"));
            }
        } catch (Exception e) {
            sendMessage((Session) session.getRemote(), new ErrorMessage(e.getMessage()));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session.getRemoteAddress());
        sessionGameMap.remove(session);
        sessionAuthMap.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Error error) throws IOException {
        System.err.println("WebSocket error for session " + session + ": " + error.getMessage());
        try {
            sendMessage(session, new ErrorMessage("WebSocket error: " + error.getMessage()));
        } catch (IOException e) {
            System.err.println("Failed to send error message to client.");
        }
    }

    private void handleConnect(Session session, String username, UserGameCommand command) throws IOException {
        // Implement
    }

    private void handleMakeMove(Session session, String username, MakeMove command) throws IOException {
        // Implement
    }

    private void handleLeave(Session session, String username, Leave command) throws IOException {
        // Implement
    }

    private void handleResign(Session session, String username, Resign command) throws IOException {
        // Implement
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
}
