package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import requestresult.CreateRequest;
import requestresult.CreateResult;
import requestresult.ListResult;
import requestresult.LogoutResult;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;

    public PostLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
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

    public String logout(String... params) throws ResponseException {
        if (params.length >= 1) {
            var authToken = params[0];
            LogoutResult result = server.logout(authToken);
            return String.format("Logged Out Successfully %s.", result.message());
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            var gameName = params[0];
            CreateRequest createRequest = new CreateRequest(gameName);
            CreateResult result = server.createGame(createRequest);
            return String.format("Created Game with ID: %s. ", result.gameID());
        }
        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
    }

    public String listGames() throws ResponseException {
        ListResult result = server.listGames();
        var gson = new Gson();
        return gson.toJson(result);
    }

    public String playGame(String... params) throws ResponseException {
        if (params.length == 1) {
            return "Just Draw Board For Time Being";
        }
        throw new ResponseException(400, "Expected: ");
    }

    public String observeGame() throws ResponseException {
        return "Just Draw Board For Time Being";
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
