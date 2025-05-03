package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements PieceMovesCalculator {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    // override hash() and equals() code
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (getPieceType() == PieceType.BISHOP)
            return BishopMoves(board, myPosition);
        throw new RuntimeException("Error with PieceType");
    }

    @Override
    public Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    @Override
    public Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // calculate moves that go up and to the right
        while (row <= 7 && column <= 7) { // can calculate a move if neither column nor row have exceeded 7
            row++;
            column++;
            ChessPosition endPosition = new ChessPosition(row, column); // creates new position
            ChessPiece occupant = board.getPiece(endPosition); // check what piece is at the end position
            if (occupant == null) {
                ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                moves.add(move); // add ChessMove to ArrayList
            }
            else {
                if (occupant.getTeamColor() != piece.getTeamColor()) { // if spot is an enemy, you can take the spot
                    ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                    moves.add(move); // add ChessMove to ArrayList
                }
                // if spot is your teammate you cannot move there
                // you cannot move past any occupant as a bishop so break out of while loop
                break;
            }
        }

        // reset starting position
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // calculate moves that go down and to the right
        while (row >= 2 && column <= 7) { // can calculate a move if row hasn't gone below 2 and column hasn't exceeded 7
            row--;
            column++;
            ChessPosition endPosition = new ChessPosition(row, column); // creates new position
            ChessPiece occupant = board.getPiece(endPosition); // check what piece is at the end position
            if (occupant == null) {
                ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                moves.add(move); // add ChessMove to ArrayList
            }
            else {
                if (occupant.getTeamColor() != piece.getTeamColor()) { // if spot is an enemy, you can take the spot
                    ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                    moves.add(move); // add ChessMove to ArrayList
                }
                // you cannot move past any occupant as a bishop so break out of while loop
                break;
            }
        }

        // reset starting position
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // calculate moves that go down and to the left
        while (row >= 2 && column >= 2) { // can calculate a move if neither row nor column has gone bleow 2
            row--;
            column--;
            ChessPosition endPosition = new ChessPosition(row, column); // creates new position
            ChessPiece occupant = board.getPiece(endPosition); // check what piece is at the end position
            if (occupant == null) {
                ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                moves.add(move); // add ChessMove to ArrayList
            }
            else {
                if (occupant.getTeamColor() != piece.getTeamColor()) { // if spot is an enemy, you can take the spot
                    ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                    moves.add(move); // add ChessMove to ArrayList
                }
                // you cannot move past any occupant as a bishop so break out of while loop
                break;
            }
        }

        // reset starting position
        row = myPosition.getRow();
        column = myPosition.getColumn();
        // calculate moves that go up and to the left
        while (row <= 7 && column >= 2) { // can calculate a move if row has not exceeded 7 and column hasn't gone below 2
            row++;
            column--;
            ChessPosition endPosition = new ChessPosition(row, column); // creates new position
            ChessPiece occupant = board.getPiece(endPosition); // check what piece is at the end position
            if (occupant == null) {
                ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                moves.add(move); // add ChessMove to ArrayList
            }
            else {
                if (occupant.getTeamColor() != piece.getTeamColor()) { // if spot is an enemy, you can take the spot
                    ChessMove move = new ChessMove(myPosition, endPosition, PieceType.BISHOP); // create ChessMove datatype from start position and end position
                    moves.add(move); // add ChessMove to ArrayList
                }
                // you cannot move past any occupant as a bishop so break out of while loop
                break;
            }
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    @Override
    public Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    @Override
    public Collection<ChessMove> QueenMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    @Override
    public Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }
}
