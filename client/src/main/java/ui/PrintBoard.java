package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PrintBoard {

    ChessBoard board;
    private boolean reversed = false;

    PrintBoard (ChessBoard board) {
        this.board = board;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public void printBoard() {
        Collection<String> empty = new ArrayList<>();
        printHighlightedBoard(empty);
    }

    private String getSymbol(ChessPiece piece) {
        String type = piece.getPieceType().toString().toUpperCase();
        String color = piece.getTeamColor().toString().toUpperCase();
        String pieceName = color + "_" + type;
        return switch (pieceName) {
            case "WHITE_KING" -> EscapeSequences.BLACK_KING;
            case "WHITE_QUEEN" -> EscapeSequences.BLACK_QUEEN;
            case "WHITE_ROOK" -> EscapeSequences.BLACK_ROOK;
            case "WHITE_KNIGHT" -> EscapeSequences.BLACK_KNIGHT;
            case "WHITE_BISHOP" -> EscapeSequences.BLACK_BISHOP;
            case "WHITE_PAWN" -> EscapeSequences.BLACK_PAWN;

            case "BLACK_KING" -> EscapeSequences.WHITE_KING;
            case "BLACK_QUEEN" -> EscapeSequences.WHITE_QUEEN;
            case "BLACK_ROOK" -> EscapeSequences.WHITE_ROOK;
            case "BLACK_KNIGHT" -> EscapeSequences.WHITE_KNIGHT;
            case "BLACK_BISHOP" -> EscapeSequences.WHITE_BISHOP;
            case "BLACK_PAWN" -> EscapeSequences.WHITE_PAWN;
            default -> "?";
        };
    }

    private void printColumnLabels(String displayCols) {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY);
        for (String col : displayCols.split("")) {
            if ((!reversed && col.equals("h")) || (reversed && col.equals("a"))) {
                System.out.print("\u202F\u2006" + col + " \u2009");
            }
            else {
                System.out.print("\u202F\u2006" + col + " \u202f\u2009\u200a");
            }
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR);
    }

    public void printHighlightedBoard(Collection<String> spacesToHighlight) {
        String colLabels = "abcdefgh";
        String displayCols = reversed ? new StringBuilder(colLabels).reverse().toString() : colLabels;
        printColumnLabels(displayCols);
        int[] rows = reversed ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};
        int[] cols = reversed ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        for (int row : rows) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + " " + row + " " + EscapeSequences.RESET_BG_COLOR);
            for (int col : cols) {
                ChessPosition currentPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currentPos);
                boolean isLightSquare = (row + col) % 2 == 1;
                String boardSpace = getBoardSpace(col, row);
                boolean highlight = spacesToHighlight.contains(boardSpace);
                String bgColor = highlight ? EscapeSequences.SET_BG_COLOR_GREEN :
                        (isLightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY);
                String symbol = (piece != null) ? getSymbol(piece) : EscapeSequences.EMPTY;

                System.out.print(bgColor + symbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + " " + row + " " + EscapeSequences.RESET_BG_COLOR);
        }
        printColumnLabels(displayCols);
    }

    private String getBoardSpace(int col, int row) {
        char file = (char) ('a' + col - 1);
        return String.valueOf(file) + row;
    }
}
