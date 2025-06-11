package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import exception.ResponseException;
import model.AuthData;
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

@WebSocket
public class WebsocketHandler {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(UserGameCommand.class, new UserGameCommandDeserializer())
            .create();
    private final UserDao userDao = Server.getUserDao();
    private final GameDao gameDao = Server.getGameDao();
    private final AuthDao authDao = Server.getAuthDao();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress());
        Server.gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session.getRemoteAddress());
        Server.gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, DataAccessException {
        try {
            System.out.println("Incoming WebSocket message: " + message);
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            Server.gameSessions.replace(session, command.getGameID());
            AuthData auth = authDao.getAuth(command.getAuthToken());
            if (auth == null) {
                sendMessage(session, new ErrorMessage("Unauthorized"));
                return;
            }
            String username = auth.username();

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, username, command);
                case MAKE_MOVE -> handleMakeMove(session, username, (MakeMove) command);
                case LEAVE -> handleLeave(session, username, (Leave) command);
                case RESIGN -> handleResign(session, username, (Resign) command);
                default -> sendMessage(session, new ErrorMessage("Unknown command type"));
            }
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) throws IOException {
        System.err.println("WebSocket error for session " + session + ": " + error.getMessage());
        try {
            sendMessage(session, new ErrorMessage("WebSocket error: " + error.getMessage()));
        } catch (IOException e) {
            System.err.println("Failed to send error message to client.");
        }
    }

    private void handleConnect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        if (gameData == null) {
            sendMessage(session, new ErrorMessage("Game not found"));
            return;
        }
        ChessGame game = gameData.game();
        if (game == null) {
            sendMessage(session, new ErrorMessage("Game not found"));
            return;
        }

        Server.gameSessions.put(session, gameID);
        sendMessage(session, new LoadGameMessage(game));

        String message;
        if (username.equals(gameData.whiteUsername())) {
            message = username + " joined the game as white.";
        } else if (username.equals(gameData.blackUsername())) {
            message = username + " joined the game as black.";
        } else {
            message = username + " is observing the game.";
        }

        for (Map.Entry<Session, Integer> entry : Server.gameSessions.entrySet()) {
            Session otherSession = entry.getKey();
            Integer otherGameID = entry.getValue();

            if (otherGameID == gameID && !otherSession.equals(session)) {
                sendMessage(otherSession, new Notification(message));
            }
        }
    }

    private void handleMakeMove(Session session, String username, MakeMove command) throws IOException, DataAccessException, InvalidMoveException {
        Integer gameID = Server.gameSessions.get(session);
        ChessMove move = command.getMove();
        if (gameID == null) {
            sendMessage(session, new ErrorMessage("Game doesn't exist"));
            return;
        }
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();

        if (isObserver(gameData, username)) {
            sendMessage(session, new ErrorMessage("Observer cannot make moves"));
            return;
        }

        if (game.getGameOver()) {
            sendMessage(session, new ErrorMessage("Game is already over"));
            return;
        }

        ChessGame.TeamColor currentTeam = game.getTeamTurn();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if ((currentTeam == ChessGame.TeamColor.WHITE && !isWhite) ||
                (currentTeam == ChessGame.TeamColor.BLACK && !isBlack)) {
            sendMessage(session, new ErrorMessage("Error: Not your turn"));
            return;
        }

        try {
            game.makeMove(move);
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("Illegal move"));
            return;
        }

        gameDao.updateGame(gameID, game);

        broadcastToGame(gameID, new LoadGameMessage(game));

        String moveMessage = String.format("%s moved from %s to %s",
                username, move.getStartPosition(), move.getEndPosition());
        broadcastToGameExcept(gameID, session, new Notification(moveMessage));

        if (game.isInCheckmate(game.getTeamTurn())) {
            game.setGameOver(true);
            gameDao.updateGame(gameID, game);
            broadcastToGame(gameID, new Notification("Checkmate! " + username + " wins."));
        } else if (game.isInStalemate(game.getTeamTurn())) {
            game.setGameOver(true);
            gameDao.updateGame(gameID, game);
            broadcastToGame(gameID, new Notification("Stalemate! The game is a draw."));
        } else if (game.isInCheck(game.getTeamTurn())) {
            broadcastToGame(gameID, new Notification("Check! " + game.getTeamTurn() + " is in check."));
        }
    }

    private void handleLeave(Session session, String username, Leave command) throws IOException, DataAccessException {
        Integer gameID = Server.gameSessions.get(session);
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
            gameDao.updatePlayerColor(gameID, "whiteUsername", null);
            isObserver = false;
            role = "WHITE";
        }
        else if (username.equals(gameData.blackUsername())) {
            gameDao.updatePlayerColor(gameID, "blackUsername", null);
            isObserver = false;
            role = "BLACK";
        }

        role = isObserver ? "observer" : role;
        String message = String.format("%s (%s) left the game", username, role);
        broadcastToGameExcept(gameID, session, new Notification(message));
        Server.gameSessions.remove(session);
    }

    private void handleResign(Session session, String username, Resign command) throws IOException, DataAccessException {
        Integer gameID = Server.gameSessions.get(session);
        if (gameID == null) {
            sendMessage(session, new ErrorMessage("Game doesn't exist"));
            return;
        }
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();

        if (isObserver(gameData, username)) {
            sendMessage(session, new ErrorMessage("Observer Cannot Resign"));
            return;
        }

        if (game.getGameOver()) {
            sendMessage(session, new ErrorMessage("Game is already over"));
            return;
        }

        game.setGameOver(true);
        gameDao.updateGame(gameID, game);

        String message = username + " has resigned. Game Over";
        broadcastToGame(gameID, new Notification(message));

    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket Not Open");
        }
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void broadcastToGame(int gameID, ServerMessage message) throws IOException {
        for (Map.Entry<Session, Integer> entry : Server.gameSessions.entrySet()) {
            if (entry.getValue() == gameID) {
                sendMessage(entry.getKey(), message);
            }
        }
    }

    private void broadcastToGameExcept(int gameID, Session excludedSession, ServerMessage message) throws IOException {
        for (Map.Entry<Session, Integer> entry : Server.gameSessions.entrySet()) {
            Session session = entry.getKey();
            Integer currentGameID = entry.getValue();

            if (currentGameID == gameID && !session.equals(excludedSession)) {
                sendMessage(session, message);
            }
        }
    }

    private boolean isObserver(GameData gameData, String username) {
        boolean observer = true;
        if (username.equals(gameData.whiteUsername())) {
            observer = false;
        }
        else if (username.equals(gameData.blackUsername())) {
            observer = false;
        }
        return observer;
    }
}
