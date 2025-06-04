package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import requestresult.*;

import java.util.Arrays;
import java.util.HashSet;

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
                case "join" -> joinGame(params);
                case "observe" -> observeGame();
                case "quit" -> "quit";
                case "help" -> help();
                default -> "Invalid Input. Type help";
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
        if (params.length == 1) {
            var gameName = params[0];
            CreateRequest createRequest = new CreateRequest(gameName);
            CreateResult result = server.createGame(createRequest, authToken);
            return String.format("Created Game with ID: %s. ", result.gameID());
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String listGames() throws ResponseException {
        try {
            ListResult result = server.listGames(authToken);
            return printGameData(result.games());
        } catch (ResponseException e) {
            throw new ResponseException(400, "No Games To List");
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            var playerColor = params[0];
            int gameID;
            try {
                gameID = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            JoinRequest joinRequest = new JoinRequest(playerColor, gameID);
            JoinResult result = server.joinGame(joinRequest, authToken);
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
               observe <PIECE_COLOR> <ID> - a game
               logout - when you are done
               quit - playing chess
               help - with possible commands
               """;
    }

    private String printGameData(HashSet<GameData> games) {
        StringBuilder allGames = new StringBuilder();
        for (GameData game : games) {
            String white = game.whiteUsername() != null ? game.whiteUsername() : "EmptySpot";
            String black = game.blackUsername() != null ? game.blackUsername() : "EmptySpot";
            String newGameLine = "GameName: " + game.gameName() + "    GameID: " + game.gameID() +
                    "    White: " + white + "    Black: " + black + '\n';
            allGames.append(newGameLine);
        }
        return allGames.toString();
    }
}
