package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
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

    private void handleConnect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        int gameID = command.getGameID();

        sessionGameMap.put(session, gameID);
        sessionAuthMap.put(session, command.getAuthToken());

        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();
        if (game == null) {
            sendMessage(session, new ErrorMessage("Game not found"));
            return;
        }

        sendMessage(session, new LoadGameMessage(game));

        String message;
        if (username.equals(gameData.whiteUsername())) {
            message = username + " joined the game as white.";
        } else if (username.equals(gameData.blackUsername())) {
            message = username + " joined the game as black.";
        } else {
            message = username + " is observing the game.";
        }

        for (Map.Entry<Session, Integer> entry : sessionGameMap.entrySet()) {
            Session otherSession = entry.getKey();
            Integer otherGameID = entry.getValue();

            if (otherGameID == gameID && !otherSession.equals(session)) {
                sendMessage(otherSession, new Notification(message));
            }
        }
    }

    private void handleMakeMove(Session session, String username, MakeMove command) throws IOException {
        // Implement
    }

    private void handleLeave(Session session, String username, Leave command) throws IOException, DataAccessException {
        Integer gameID = sessionGameMap.get(session);
        if (gameID == null) {
            sendMessage(session, new ErrorMessage("Game doesn't exist"));
            return;
        }

        GameData gameData = gameDao.getGame(gameID);
        if (gameData == null) {
            sendMessage(session, new ErrorMessage("Game not found"));
            return;
        }

        boolean isObserver = true;
        String role = "player";
        if (username.equals(gameData.whiteUsername())) {
            gameDao.updatePlayerColor(gameID, "WHITE", null);
            isObserver = false;
            role = "WHITE";
        }
        else if (username.equals(gameData.blackUsername())) {
            gameDao.updatePlayerColor(gameID, "BLACK", null);
            isObserver = false;
            role = "BLACK";
        }

        sessionGameMap.remove(session);
        sessionAuthMap.remove(session);

        role = isObserver ? "observer" : role;
        String message = String.format("%s (%s) left the game", username, role);
        broadcastToGameExcept(gameID, session, new Notification(message));
    }

    private void handleResign(Session session, String username, Resign command) throws IOException {
        // Implement
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void broadcastToGameExcept(int gameID, Session excludedSession, ServerMessage message) throws IOException {
        for (Map.Entry<Session, Integer> entry : sessionGameMap.entrySet()) {
            Session session = entry.getKey();
            Integer currentGameID = entry.getValue();

            if (currentGameID == gameID && !session.equals(excludedSession)) {
                sendMessage(session, message);
            }
        }
    }

    /// Notifications:
    // 1 - User connects and message displays Player's name and team color              THIS IS COMPLETE!
    // 2 - User connect as observer and display's observer's name                       THIS IS COMPLETED!
    // 3 - User makes a move. Display player's name and move (board updates)
    // 4 - User leaves a game. Display user's name
    // 5 - User resigns. Display user's name
    // 6 - Player is in check. Display user's name
    // 7 - Player is in checkmate. Display user's name
}
