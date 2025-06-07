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
}
