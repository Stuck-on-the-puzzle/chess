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
        if (getPieceType() == PieceType.PAWN)
            return PawnMoves(board, myPosition);
        else if (getPieceType() == PieceType.BISHOP)
            return BishopMoves(board, myPosition);
        else if (getPieceType() == PieceType.ROOK)
            return RookMoves(board, myPosition);
        else if (getPieceType() == PieceType.KNIGHT)
            return KnightMoves(board, myPosition);
        else if (getPieceType() == PieceType.QUEEN)
            return QueenMoves(board, myPosition);
        else if (getPieceType() == PieceType.KING)
            return KingMoves(board, myPosition);
        throw new RuntimeException("Error with PieceType");
    }

    @Override
    public Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        // if pawn is on white team
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition endPosition; // create ChessPosition datatype
            ChessMove move; // create ChessMove datatype from start position and end position
            if (myPosition.getRow() == 2) { // if white pawn is in starting position it can move two spots
                endPosition = new ChessPosition(row + 2, column); // creates new position
                ChessPiece occupant = board.getPiece(endPosition);
                ChessPosition closePosition = new ChessPosition(row + 1, column);
                ChessPiece closeOccupant = board.getPiece(closePosition);
                if (occupant == null && closeOccupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                }
            }

            if (myPosition.getRow() < 8){ // checking if enemy can be captured
                endPosition = new ChessPosition(row + 1, column);
                ChessPiece occupant = board.getPiece(endPosition);
                if (occupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                }
                ChessPosition adjPosLeft = null;
                ChessPosition adjPosRight = null;
                if (myPosition.getColumn() > 1) {
                    adjPosLeft = new ChessPosition(row + 1, column - 1);
                }
                if (myPosition.getColumn() < 8) {
                    adjPosRight = new ChessPosition(row + 1, column + 1);
                }
                if (adjPosLeft != null) {
                    ChessPiece occupantLeft = board.getPiece(adjPosLeft); // check what piece is at the adjLeft position
                    if (occupantLeft != null) {
                        if (occupantLeft.getTeamColor() == ChessGame.TeamColor.BLACK) {
                            move = new ChessMove(myPosition, adjPosLeft, PieceType.PAWN);
                            moves.add(move);
                            if (myPosition.getRow() + 1 == 8) {
                                moves.add(move);
                                moves.add(move);
                                moves.add(move);
                            }
                        }
                    }
                }
                if (adjPosRight != null) {
                    ChessPiece occupantRight = board.getPiece(adjPosRight); // check what piece is at the adjRight position
                    if (occupantRight != null) {
                        if (occupantRight.getTeamColor() == ChessGame.TeamColor.BLACK) {
                            move = new ChessMove(myPosition, adjPosRight, PieceType.PAWN);
                            moves.add(move);
                            if (myPosition.getRow() + 1 == 8) {
                                moves.add(move);
                                moves.add(move);
                                moves.add(move);
                            }
                        }
                    }
                }
            }

            if (myPosition.getRow() + 1 == 8) { // move is added for each version of promotion (first one is already added)
                endPosition = new ChessPosition(row + 1, column);
                ChessPiece occupant = board.getPiece(endPosition);
                if (occupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                    moves.add(move);
                    moves.add(move);
                }
            }
        }

        // if pawn is on black team
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            ChessPosition endPosition; // create ChessPosition datatype
            ChessMove move; // create ChessMove datatype from start position and end position
            if (myPosition.getRow() == 7) { // if black pawn is in starting position it can move two spots
                endPosition = new ChessPosition(row - 2, column); // creates new position
                ChessPiece occupant = board.getPiece(endPosition);
                ChessPosition closePosition = new ChessPosition(row - 1, column);
                ChessPiece closeOccupant = board.getPiece(closePosition);
                if (occupant == null && closeOccupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                }
            }

            if (myPosition.getRow() > 1) { // check if enemy can be captured
                endPosition = new ChessPosition(row - 1, column);
                ChessPiece occupant = board.getPiece(endPosition);
                if (occupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                }
                ChessPosition adjPosLeft = null;
                ChessPosition adjPosRight = null;
                if (myPosition.getColumn() > 1) {
                    adjPosLeft = new ChessPosition(row - 1, column - 1);
                }
                if (myPosition.getColumn() < 8) {
                    adjPosRight = new ChessPosition(row - 1, column + 1);
                }
                if (adjPosLeft != null) {
                    ChessPiece occupantLeft = board.getPiece(adjPosLeft); // check what piece is at the adjLeft position
                    if (occupantLeft != null) {
                        if (occupantLeft.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            move = new ChessMove(myPosition, adjPosLeft, PieceType.PAWN);
                            moves.add(move);
                            if (myPosition.getRow() - 1 == 1) {
                                moves.add(move);
                                moves.add(move);
                                moves.add(move);
                            }
                        }
                    }
                }
                if (adjPosRight != null) {
                    ChessPiece occupantRight = board.getPiece(adjPosRight); // check what piece is at the adjRight position
                    if (occupantRight != null) {
                        if (occupantRight.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            move = new ChessMove(myPosition, adjPosRight, PieceType.PAWN);
                            moves.add(move);
                            if (myPosition.getRow() - 1 == 1) {
                                moves.add(move);
                                moves.add(move);
                                moves.add(move);
                            }
                        }
                    }
                }
            }

            if (myPosition.getRow() - 1 == 1) { // move is added for each version of promotion (first one is already added)
                endPosition = new ChessPosition(row - 1, column);
                ChessPiece occupant = board.getPiece(endPosition);
                if (occupant == null) {
                    move = new ChessMove(myPosition, endPosition, PieceType.PAWN); // create ChessMove datatype from start position and end position
                    moves.add(move);
                    moves.add(move);
                    moves.add(move);
                }

            }
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        // calculate moves that go up and to the right
        while (row <= 8 && column <= 8) { // can calculate a move if neither column nor row have exceeded 7
            row++;
            column++;
            if (row > 8 || column > 8) {
                break;
            }
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
        while (row >= 1 && column <= 8) { // can calculate a move if row hasn't gone below 2 and column hasn't exceeded 7
            row--;
            column++;
            if (row < 1 || column > 8) {
                break;
            }
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
        while (row >= 1 && column >= 1) { // can calculate a move if neither row nor column has gone bleow 2
            row--;
            column--;
            if (row < 1 || column < 1) {
                break;
            }
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
        while (row <= 8 && column >= 1) { // can calculate a move if row has not exceeded 7 and column hasn't gone below 2
            row++;
            column--;
            if (row > 8 || column < 1) {
                break;
            }
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
