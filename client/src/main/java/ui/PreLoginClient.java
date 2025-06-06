package ui;

import exception.ResponseException;
import model.UserData;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import requestresult.RegisterResult;

import java.util.Arrays;

public class PreLoginClient {
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "clear" -> clear(); // needs to be taken out by the end of the project
                case "quit" -> "quit";
                case "help" -> help();
                default -> "Invalid Input. Type help";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var username = params[0];
            var password = params[1];
            var loginRequest = new LoginRequest(username, password);
            LoginResult result = server.login(loginRequest);
            return String.format("Logged in as %s with authToken %s", result.username(), result.authToken());
        }
        throw new ResponseException(400, "Expected:login <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var userData = new UserData(username, password, email);
            RegisterResult result = server.register(userData);
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = server.login(loginRequest);
            return String.format("Logged in as %s with authToken %s", result.username(), result.authToken());
        }
        throw new ResponseException(400, "Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String clear() throws ResponseException {
        server.clear();
        return "All Data Cleared!";
    }

    public String help() {
        return """
               register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               login <USERNAME> <PASSWORD> - to play chess
               clear - all data
               quit - playing chess
               help - with possible commands
               """;
    }

}
