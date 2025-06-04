package ui;

import java.util.Scanner;

import static java.awt.Color.GREEN;

public class Repl {

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
//    private final GameplayClient gameplayClient;
    private String state = "Logged Out";
    private String username;
    private String authToken;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
//        gameplayClient = new GameplayClient(serverUrl);
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
                        username = parts[3].replace(".", "");
                        state = "Logged in";
                    }
                }

                else if (state.equals("Logged in")) {
                    result = postLoginClient.eval(line);

                    if (line.toLowerCase().startsWith("logout")) {
                        state = "Logged Out";
                        username = null;
                        authToken = null;
                    }
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
        }
    }
}
