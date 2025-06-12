package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requestresult.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;
    private HashSet<GameData> games;
    private HashMap<Integer, Integer> localToServerGameIDs = new HashMap<>();
    private int nextGameNumber = 1;

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
            localToServerGameIDs.put(nextGameNumber++, result.gameID());
            return "Game Created";
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String listGames() throws ResponseException {
        try {
            ListResult result = server.listGames(authToken);
            games = result.games();
            for (GameData game : games) {
                int serverID = game.gameID();
                if (!localToServerGameIDs.containsValue(serverID)) {
                    localToServerGameIDs.put(nextGameNumber++, serverID);
                }
            }

            return printGameData(games);
        } catch (ResponseException e) {
            throw new ResponseException(400, "No Games To List");
        }
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            refreshGames();
            var playerColor = params[0].toUpperCase();
            int localNumber;
            try {
                localNumber = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid Game Number");
            }
            int gameID = getGameIDNumberFromLocal(localNumber);
            JoinRequest joinRequest = new JoinRequest(playerColor, gameID);
            server.joinGame(joinRequest, authToken);
            return String.format("Joined Game! %d with authToken %s", gameID, authToken);
        }
        throw new ResponseException(400, "Expected: join <PIECE_COLOR> <ID>");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            refreshGames();
            int localNumber;
            try {
                 localNumber = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Invalid Game Number");
            }
            int gameID = getGameIDNumberFromLocal(localNumber);
            return String.format("Observing Game! %d %s %d", gameID, authToken, localNumber);
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

    private String printGameData(HashSet<GameData> games) throws ResponseException {
        StringBuilder allGames = new StringBuilder();
        for (GameData game : games) {
            String white = game.whiteUsername() != null ? game.whiteUsername() : "EmptySpot";
            String black = game.blackUsername() != null ? game.blackUsername() : "EmptySpot";
            String newGameLine = "GameNumber: " + getLocalNumberFromGameID(game.gameID()) +"    GameName: " + game.gameName() +
                    "    White: " + white + "    Black: " + black + '\n';
            allGames.append(newGameLine);
        }
        return allGames.toString();
    }

    private void printBoard(int gameID, String color) throws ResponseException{
        GameData game = getGame(gameID);
        ChessGame selectedGame = game.game();
        PrintBoard board = new PrintBoard(selectedGame.getBoard());
        if (color.equalsIgnoreCase("BLACK")) {
            board.setReversed(true);
        }
        board.printBoard();
    }

    public GameData getGame(int gameID) throws ResponseException {
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
        return selectedGame;
    }

    private int getLocalNumberFromGameID(int gameID) throws ResponseException {
        for (var entry : localToServerGameIDs.entrySet()) {
            if (entry.getValue() == gameID) {
                return entry.getKey();
            }
        }
        throw new ResponseException(400, "Game Not Found");
    }

    private int getGameIDNumberFromLocal(int localNumber) throws ResponseException {
        Integer serverID = localToServerGameIDs.get(localNumber);
        if(serverID != null) {
            return serverID;
        }
        throw new ResponseException(400, "Game Not Found");
    }
}
