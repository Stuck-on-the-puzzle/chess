package ui;

import Websocket.ServerMessageObserver;
import Websocket.WebSocketFacade;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requestresult.JoinRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

import static javax.swing.UIManager.get;

public class GameplayClient {

    private final WebSocketFacade ws;
    private final ServerMessageObserver observer;
    private final String serverUrl;
    private String authToken;
    private int gameID;
    private ChessGame game;

    public GameplayClient(String serverUrl, ServerMessageObserver observer) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, observer);
        this.serverUrl = serverUrl;
        this.observer = observer;
    }

    public void setAuth(String authToken, int gameID) {
        this.authToken =authToken;
        this.gameID = gameID;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "highlight" -> highlightLegalMoves(params);
                case "move" -> makeMove(params);
                case "leave" -> leave();
                case "resign" -> resign();
                case "help" -> help();
                default -> "";
            };
        } catch (ResponseException | IOException ex) {
            return ex.getMessage();
        }
    }

    public String redrawBoard() throws ResponseException {
        return "Redrawn";
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        if (params.length == 2) {
            return "Highlighted";
        }
        throw new ResponseException(400, "Expected: <LEGAL SPACE>");
    }

    public String makeMove(String... params) throws ResponseException, IOException {
        if (params.length == 2) {
            ws.makeMove(authToken, gameID);
            return "Moved";
        }
        throw new ResponseException(400, "Expected: <FROM> <TO> <PROMOTION PIECE (if applicable)>");
    }

    public String leave() throws ResponseException {
        try {
            ws.leave(authToken, gameID);
            return "Left game but make sure this is websocket";
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to leave game");
        }
    }

    public String resign() throws ResponseException {
        try {
            ws.resign(authToken, gameID);
            return "Resigned from game but make sure this is websocket";
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to resign");
        }
    }

    public String help() {
        return """
               redraw - the current board
               highlight - legal moves
               move - a chess piece
               leave - the game
               resign - from the game
               help - with possible commands
               """;
    }

    /// Notifications:
    // 1 - User connects and message displays Player's name and team color
    // 2 - User connect as observer and display's observer's name
    // 3 - User makes a move. Display player's name and move (board updates)
    // 4 - User leaves a game. Display user's name
    // 5 - User resigns. Display user's name
    // 6 - Player is in check. Display user's name
    // 7 - Player is in checkmate. Display user's name

    /// Gameplay functionality:
    // Help
    // Redraw Chess Board
    // Leave
    // Make Move
    // Resign
    // Highlight Legal Moves
}
