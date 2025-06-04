package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import requestresult.*;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;

    public PostLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        if (authToken != null && !authToken.isBlank()) {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            server.logout(logoutRequest);
            return "Logged Out Successfully.";
        }
        throw new ResponseException(400, "Error Logging Out");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            var gameName = params[0];
            CreateRequest createRequest = new CreateRequest(gameName);
            CreateResult result = server.createGame(createRequest);
            return String.format("Created Game with ID: %s. ", result.gameID());
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String listGames() throws ResponseException {
        try {
            ListResult result = server.listGames();
            var gson = new Gson();
            return gson.toJson(result);
        } catch (ResponseException e) {
            throw new ResponseException(400, "No Games To List");
        }
    }

    public String playGame(String... params) throws ResponseException {
        if (params.length == 1) {
            return "Just Draw Board For Time Being";
        }
        throw new ResponseException(400, "Expected: join <ID>");
    }

    public String observeGame() throws ResponseException {
        return "Just Draw Board For Time Being";
//        throw new ResponseException(400, "Expected: observe <ID>");
    }

    public String help() {
        return """
               create <NAME> - a game
               list - games
               join <ID> - a game
               observe <ID> - a game
               logout - when you are done
               quit - playing chess
               help - with possible commands
               """;
    }
}
