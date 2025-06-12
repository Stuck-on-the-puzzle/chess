package ui;

import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.io.IOException;
import java.util.*;

public class GameplayClient {

    private final WebSocketFacade ws;
    private final ServerMessageObserver observer;
    private final String serverUrl;
    private PrintBoard printBoard;
    private String authToken;
    private int gameID;
    private ChessGame game;
    private ChessGame.TeamColor playerColor;
    private final Map<String, Integer> rowMap;
    private final Map<String, Integer> colMap;

    public GameplayClient(String serverUrl, ServerMessageObserver observer) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, observer);
        this.serverUrl = serverUrl;
        this.observer = observer;
        this.rowMap = new HashMap<>();
        this.colMap = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            char letter = (char) ('a' + i);
            rowMap.put(String.valueOf(i+1), i+1);
            colMap.put(String.valueOf(letter), i+1);
        }

    }

    public void setAuth(String authToken, int gameID) {
        this.authToken =authToken;
        this.gameID = gameID;
    }

    public void connectToWs(String authToken, int gameID) throws IOException {
        ws.connect(authToken, gameID);
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.playerColor = color;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "highlight" -> highlightLegalMoves(params);
                case "move" -> makeMove(params);
                case "leave" -> leave();
                case "resign" -> resign();
                case "help" -> help();
                default -> "";
            };
        } catch (ResponseException | IOException ex) {
            return ex.getMessage();
        }
    }

    public String redrawBoard() throws ResponseException {
        printBoard = new PrintBoard(game.getBoard());
        printBoard.setReversed(this.playerColor == ChessGame.TeamColor.BLACK);
        printBoard.printBoard();
        return "Current Board";
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        if (params.length == 1) {
            ChessPosition pos = getChessPosition(params[0]);
            Collection<String> spacesToHighlight = getBoardSpaces(pos, game.validMoves(pos));
            printBoard = new PrintBoard(game.getBoard());
            printBoard.setReversed(this.playerColor == ChessGame.TeamColor.BLACK);
            printBoard.printHighlightedBoard(spacesToHighlight);
            return "Highlighted Moves";
        }
        throw new ResponseException(400, "Expected: <LEGAL SPACE>");
    }

    public String makeMove(String... params) throws ResponseException, IOException {
        if (params.length >= 2) {
            String start = params[0];
            String stop = params[1];
            String promotion;
            if (params.length > 2) {
                promotion = params[2];
            }
            else {
                promotion = null;
            }
            ChessMove move = getChessMove(start, stop, promotion);
            ws.makeMove(authToken, gameID, move);
            return "Moving...";
        }
        throw new ResponseException(400, "Expected: <FROM> <TO> <PROMOTION PIECE (if applicable)>");
    }

    public String leave() throws ResponseException {
        try {
            ws.leave(authToken, gameID);
            return "Left game";
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to leave game");
        }
    }

    public String resign() throws ResponseException {
        try {
            ws.resign(authToken, gameID);
            return "";
        } catch (IOException e) {
            throw new ResponseException(500, "Failed to resign");
        }
    }

    public String help() {
        return """
               redraw - the current board
               highlight - legal moves
               move - a chess piece
               leave - the game
               resign - from the game
               help - with possible commands
               """;
    }

    private ChessPosition getChessPosition(String pos) throws ResponseException {
        String colString = String.valueOf(pos.charAt(0));
        String rowString = String.valueOf(pos.charAt(1));
        if (!rowMap.containsKey(rowString) || !colMap.containsKey(colString)) {
            throw new ResponseException(500, "Invalid Spot");
        }
        int row = rowMap.get(rowString);
        int col = colMap.get(colString);
        return new ChessPosition(row,col);
    }

    private ChessPiece.PieceType getPromotionPiece(String promotion) {
        if (promotion == null) {
            return null;
        }
        return switch(promotion.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            default -> null;
        };
    }

    private ChessMove getChessMove(String start, String stop, String promotion) throws ResponseException {
        ChessPiece.PieceType piece = getPromotionPiece(promotion);
        if (start.length() != 2 || stop.length() != 2) {
            throw new ResponseException(500, "Invalid Move");
        }
        ChessPosition startPos = getChessPosition(start);
        ChessPosition stopPos = getChessPosition(stop);
        return new ChessMove(startPos, stopPos, piece);
    }

    private Collection<String> getBoardSpaces(ChessPosition currPos, Collection<ChessMove> moves) {
        Map<Integer, String> reverseColMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
            reverseColMap.put(entry.getValue(), entry.getKey());
        }
        Collection<String> positions = new HashSet<>();
        for (ChessMove  move : moves) {
            ChessPosition pos = move.getEndPosition();
            String col = reverseColMap.get(pos.getColumn());
            String row = String.valueOf(pos.getRow());
            positions.add(col + row);
        }
        String currCol = reverseColMap.get(currPos.getColumn());
        String currRow = String.valueOf(currPos.getRow());
        positions.add(currCol + currRow);
        return positions;
    }
}
