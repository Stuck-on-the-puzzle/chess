package ui;

import exception.ResponseException;
import requestresult.CreateRequest;
import requestresult.CreateResult;
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
                case "observe" -> observeGame(params);
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
            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
        }
        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
    }

    public String listGames() throws ResponseException {
        var pets = server.listPets();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var pet : pets) {
            result.append(gson.toJson(pet)).append('\n');
        }
        return result.toString();
    }

    public String playGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                var id = Integer.parseInt(params[0]);
                var pet = getPet(id);
                if (pet != null) {
                    server.deletePet(id);
                    return String.format("%s says %s", pet.name(), pet.sound());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new ResponseException(400, "Expected: <pet id>");
    }

    public String observeGame() throws ResponseException {
        assertSignedIn();
        var buffer = new StringBuilder();
        for (var pet : server.listPets()) {
            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
        }

        server.deleteAllPets();
        return buffer.toString();
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
