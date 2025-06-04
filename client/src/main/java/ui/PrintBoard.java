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
        String displayLabels = "  " + (reversed ? new StringBuilder(colLabels).reverse().toString() : colLabels);
        System.out.println(displayLabels);
        for (int row = 1; row < 9; row++) {
            int realRow = reversed ? 8- row : row;
            System.out.print(realRow + " ");
            for (int col = 1; col < 9; col++) {
                int realCol = reversed ? 8 - col : col;
                ChessPosition currentPos = new ChessPosition(realRow, realCol);
                ChessPiece piece = board.getPiece(currentPos);
                if (piece != null) {
                    System.out.print(getSymbol(piece) + " ");
                }
                else {
                    System.out.print(EscapeSequences.EMPTY);
                }
            }
            System.out.println(realRow + 1);
        }
        System.out.println(displayLabels);
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
}
