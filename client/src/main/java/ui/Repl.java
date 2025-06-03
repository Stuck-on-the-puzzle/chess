package ui;

import java.util.Scanner;

import static java.awt.Color.GREEN;

public class Repl {

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
//    private final GameplayClient gameplayClient;
    private String state = "Logged Out";

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
//        gameplayClient = new GameplayClient(serverUrl);
    }
    public void run() {
        String whiteKing = "♔";
        String blackKing = "♚";
        System.out.println("♚ Welcome to Chess. Type help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.println(GREEN + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
}
