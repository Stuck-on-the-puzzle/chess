package ui;

import Websocket.ServerMessageObserver;
import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

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
    private int gameID;

    public Repl(String serverUrl) throws ResponseException {
        this.serverUrl = serverUrl;
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        gameplayClient = new GameplayClient(serverUrl, this);
    }

    public void run() {
        String whiteKing = "♔";
        String blackKing = "♚";
        System.out.println("♚ Welcome to Chess. Type help to get started ♚");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();

            String line = scanner.nextLine();

            try {
                if (state.equals("Logged Out")) {
                    result = preLoginClient.eval(line);

                    if (result.startsWith("Logged in as")) {
                        String[] parts = result.split(" ");
                        username = parts[3];
                        authToken = parts[6];
                        postLoginClient.setAuthToken(authToken);
                        state = "Logged in";
                        result = "Logged in as " + username;
                    }
                }

                else if (state.equals("Logged in")) {
                    result = postLoginClient.eval(line);


                    if (line.toLowerCase().startsWith("join")) {
                        String[] parts = result.split(" ");
                        if (parts.length == 6) {
                            gameID = Integer.parseInt(parts[2]);
                            authToken = parts[5];
                            state = "Playing";
                            result = "Joined Game!";
                        }
                    }

                    if (line.toLowerCase().startsWith("observe")) {
                        String[] parts = result.split(" ");
                        int numID = Integer.parseInt(parts[2]);
                        state = "Observing";
                        result = "Observing Game:" + numID;

                    }

                    if (line.toLowerCase().startsWith("logout")) {
                        state = "Logged Out";
                        username = null;
                        authToken = null;
                    }
                }

                else if (state.equals("Playing")) {
                    result = gameplayClient.eval(line);
                    gameplayClient.setAuth(authToken, gameID);
                }

                else if (state.equals("Observing")) {
                    result = gameplayClient.eval(line);
                    gameplayClient.setAuth(authToken, gameID);
                }

                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (state.equals("Logged Out")) {
            System.out.print("[LOGGED OUT] >>> ");
        } else if (state.equals("Logged in")) {
            System.out.print("[LOGGED IN as " + username + "] >>> ");
        } else if (state.equals("Playing")) {
            System.out.print("[PLAYING CHESS] >>> ");
        } else if (state.equals("Observing")) {
            System.out.println("[OBSERVING CHESS] >>> ");
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                var note = (Notification) message;
                System.out.println(note.getMessage());
                printPrompt();
            }

            case LOAD_GAME -> {
                var loadMessage = (LoadGameMessage) message;
                var game = loadMessage.getGame();
                gameplayClient.setGame(game);
                System.out.println("Game Loaded.");
                boardPrinter = new PrintBoard(game.getBoard());
                boardPrinter.printBoard();
                // maybe print info such as whose turn it is?
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
}
