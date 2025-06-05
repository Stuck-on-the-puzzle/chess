package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class PrintBoard {

    ChessBoard board;
    private boolean reversed = false;

    PrintBoard (ChessBoard board) {
        this.board = board;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    void printBoard() {
        String colLabels = "abcdefgh";
        String displayCols = reversed ? new StringBuilder(colLabels).reverse().toString() : colLabels;
        printColumnLabels(displayCols);
        for (int row = 8; row > 0; row--) {
            int realRow = reversed ? 9 - row : row;
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + " " + realRow + " " + EscapeSequences.RESET_BG_COLOR);
            for (int col = 1; col < 9; col++) {
                int realCol = reversed ? 9 - col : col;
                ChessPosition currentPos = new ChessPosition(9-realRow, realCol);
                ChessPiece piece = board.getPiece(currentPos);
                boolean isLightSquare = (realRow + realCol) % 2 == 1;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                String symbol = (piece != null) ? getSymbol(piece) : EscapeSequences.EMPTY;

                System.out.print(bgColor + symbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + " " + realRow + " " + EscapeSequences.RESET_BG_COLOR);
        }
        printColumnLabels(displayCols);
    }

    private String getSymbol(ChessPiece piece) {
        String type = piece.getPieceType().toString().toUpperCase();
        String color = piece.getTeamColor().toString().toUpperCase();
        String pieceName = color + "_" + type;
        return switch (pieceName) {
            case "WHITE_KING" -> EscapeSequences.WHITE_KING;
            case "WHITE_QUEEN" -> EscapeSequences.WHITE_QUEEN;
            case "WHITE_ROOK" -> EscapeSequences.WHITE_ROOK;
            case "WHITE_KNIGHT" -> EscapeSequences.WHITE_KNIGHT;
            case "WHITE_BISHOP" -> EscapeSequences.WHITE_BISHOP;
            case "WHITE_PAWN" -> EscapeSequences.WHITE_PAWN;

            case "BLACK_KING" -> EscapeSequences.BLACK_KING;
            case "BLACK_QUEEN" -> EscapeSequences.BLACK_QUEEN;
            case "BLACK_ROOK" -> EscapeSequences.BLACK_ROOK;
            case "BLACK_KNIGHT" -> EscapeSequences.BLACK_KNIGHT;
            case "BLACK_BISHOP" -> EscapeSequences.BLACK_BISHOP;
            case "BLACK_PAWN" -> EscapeSequences.BLACK_PAWN;
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
}
