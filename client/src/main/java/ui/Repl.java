package ui;

import java.util.Scanner;

public class Repl {

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
