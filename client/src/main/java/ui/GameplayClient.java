package ui;

import Websocket.WebSocketFacade;
import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

import static javax.swing.UIManager.get;

public class GameplayClient {

    private final WebSocketFacade ws;
    private final String serverUrl;
    private String authToken;
    private ChessGame game;

    public GameplayClient(String serverUrl) {
        ws = new WebSocketFacade(serverUrl, observer);
        this.serverUrl = serverUrl;
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
