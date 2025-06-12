package ui;

import websocket.ServerMessageObserver;
import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Scanner;

public class Repl implements ServerMessageObserver {

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private PrintBoard boardPrinter;
    private final String serverUrl;
    private GameplayClient gameplayClient;
    private String state = "Logged Out";
    private String username;
    private String authToken;
    private GameData game;
    private int gameID;

    public Repl(String serverUrl) throws ResponseException {
        this.serverUrl = serverUrl;
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        gameplayClient = new GameplayClient(serverUrl, this);
    }

    public void run() {
        System.out.println("♚ Welcome to Chess. Type help to get started ♚");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();

            String line = scanner.nextLine();

            try {
                switch (state) {
                    case "Logged Out" -> result = handleLoggedOut(line);
                    case "Logged in" -> result = handleLoggedIn(line);
                    case "Playing", "Observing" -> result = handlePlayingOrObserving(line, scanner);
                    default -> result = "Unknown state";
                }

                System.out.println(result);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }

        System.out.println();
    }

    private String handleLoggedOut(String line) {
        String result = preLoginClient.eval(line);

        if (result.startsWith("Logged in as")) {
            String[] parts = result.split(" ");
            this.username = parts[3];
            this.authToken = parts[6];
            postLoginClient.setAuthToken(authToken);
            state = "Logged in";
            return "Logged in as " + username;
        }

        return result;
    }

    private String handleLoggedIn(String line) throws IOException, ResponseException {
        String result = postLoginClient.eval(line);

        String lowerLine = line.toLowerCase();

        if (lowerLine.startsWith("join")) {
            String[] parts = result.split(" ");
            if (parts.length == 6) {
                gameID = Integer.parseInt(parts[2]);
                authToken = parts[5];
                state = "Playing";
                this.game = postLoginClient.getGame(gameID);
                gameplayClient.setAuth(authToken, gameID);
                gameplayClient.connectToWs(authToken, gameID);
                return "Joined Game!";
            }
        } else if (lowerLine.startsWith("observe")) {
            String[] parts = result.split(" ");
            if (parts.length >= 5) {
                gameID = Integer.parseInt(parts[2]);
                authToken = parts[3];
                int numID = Integer.parseInt(parts[4]);
                state = "Observing";
                GameData game = postLoginClient.getGame(gameID);
                gameplayClient.setAuth(authToken, gameID);
                gameplayClient.connectToWs(authToken, gameID);
                return "Observing Game: " + numID;
            }
        } else if (lowerLine.startsWith("logout")) {
            state = "Logged Out";
            username = null;
            authToken = null;
            return "Logged out.";
        }

        return result;
    }

    private String handlePlayingOrObserving(String line, Scanner scanner) throws ResponseException {
        String lowerLine = line.toLowerCase();

        if (lowerLine.startsWith("resign")) {
            System.out.print("Are you sure you want to resign? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y") || response.equals("yes")) {
                return gameplayClient.resign();
            } else {
                return "Resign cancelled";
            }
        } else {
            String result = gameplayClient.eval(line);
            gameplayClient.setAuth(authToken, gameID);

            if (lowerLine.startsWith("leave")) {
                state = "Logged in";
            }
            return result;
        }
    }

    private void printPrompt() {
        switch (state) {
            case "Logged Out" -> System.out.print("[LOGGED OUT] >>> ");
            case "Logged in" -> System.out.print("[LOGGED IN as " + username + "] >>> ");
            case "Playing" -> System.out.print("[PLAYING CHESS] >>> ");
            case "Observing" -> System.out.print("[OBSERVING CHESS] >>> ");
            default -> System.out.print("[UNKNOWN STATE] >>> ");
        }
    }

    @Override
    public void notify(ServerMessage message) throws ResponseException {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                var note = (Notification) message;
                System.out.println(note.getMessage());
                printPrompt();
            }
            case LOAD_GAME -> {
                var loadMessage = (LoadGameMessage) message;
                GameData gameData = loadMessage.getGame();
                this.game = gameData;
                gameplayClient.setGame(gameData.game());
                System.out.println("Game Loaded.");
                ChessGame.TeamColor color = getColor();
                gameplayClient.setColor(color);
                boardPrinter = new PrintBoard(gameData.game().getBoard());
                boardPrinter.setReversed(color == ChessGame.TeamColor.BLACK);
                boardPrinter.printBoard();
                printPrompt();
            }
            case ERROR -> {
                var error = (ErrorMessage) message;
                System.out.println(error.getMessage());
                printPrompt();
            }
            default -> {
                System.out.println("Unknown Message Type.");
                printPrompt();
            }
        }
    }

    private ChessGame.TeamColor getColor() {
        if (this.username.equals(this.game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return ChessGame.TeamColor.WHITE;
        }
    }
}
