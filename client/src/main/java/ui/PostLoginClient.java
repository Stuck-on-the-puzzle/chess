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
    private HashSet<GameData> games;

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
                case "observe" -> observeGame(params);
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
            refreshGames();
            var playerColor = params[0].toUpperCase();
            int gameID;
            try {
                gameID = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            JoinRequest joinRequest = new JoinRequest(playerColor, gameID);
            JoinResult result = server.joinGame(joinRequest, authToken);
            return "Joined Game! Maybe this is supposed to print out board";
        }
        throw new ResponseException(400, "Expected: join <PIECE_COLOR> <ID>");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            refreshGames();
            int gameID;
            try {
                 gameID = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid Game ID");
            }
            GameData selectedGame = null;
            for (GameData game: games) {
                if (game.gameID() == gameID) {
                    selectedGame = game;
                    break;
                }
            }
            if (selectedGame == null) {
                throw new ResponseException(400, "Game Not Found");
            }
            System.out.println(selectedGame);
            System.out.println(selectedGame.game());
            System.out.println(selectedGame.game().getBoard());
            PrintBoard board = new PrintBoard(selectedGame.game().getBoard());
            board.printBoard();
            return "Here is the Board";
        }
        throw new ResponseException(400, "Expected: observe <ID>");

    }

    private void refreshGames() throws ResponseException{
        try {
            games = new HashSet<>();
            HashSet<GameData> gameList = server.listGames(authToken).games();
            games.addAll(gameList);
        } catch (ResponseException e) {
            throw new ResponseException(400, "Can't Refresh Games");
        }
    }

    public String help() {
        return """
               create <NAME> - a game
               list - games
               join <PIECE_COLOR> <ID> - a game
               observe <ID> - a game
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
