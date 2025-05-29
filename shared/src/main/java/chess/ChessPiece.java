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
        if (getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }
        else if (getPieceType() == PieceType.KING) {
            return kingMoves(board, myPosition);
        }
        throw new RuntimeException("Error with PieceType");
    }
    
    @Override
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        moves.addAll(pawnCalc(board, myPosition, pieceColor));
        return moves;
    }

    @Override
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        // calculate moves that go up and to the right
        moves.addAll(repeatMoveCalc(board, myPosition, 1, 1));
        // calculate moves that go up and to the left
        moves.addAll(repeatMoveCalc(board, myPosition, 1, -1));
        // calculate moves that go down and to the right
        moves.addAll(repeatMoveCalc(board, myPosition, -1, 1));
        // calculate moves that go down and to the left
        moves.addAll(repeatMoveCalc(board, myPosition, -1, -1));

        return moves;
    }

    @Override
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        // calculate moves that go up
        moves.addAll(repeatMoveCalc(board, myPosition, 1, 0));
        // calculate moves that go down
        moves.addAll(repeatMoveCalc(board, myPosition, -1, 0));
        // calculate moves that go right
        moves.addAll(repeatMoveCalc(board, myPosition, 0, 1));
        // calculate moves that go  left
        moves.addAll(repeatMoveCalc(board, myPosition, 0, -1));

        return moves;
    }

    @Override
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        // calculate possible positions
        //up-right
        moves.addAll(moveCalc(board, myPosition, +2, +1));
        //up-left
        moves.addAll(moveCalc(board, myPosition, +2, -1));
        //down-right
        moves.addAll(moveCalc(board, myPosition, -2, +1));
        //down-left
        moves.addAll(moveCalc(board, myPosition, -2, -1));
        //right-up
        moves.addAll(moveCalc(board, myPosition, +1, +2));
        //right-down
        moves.addAll(moveCalc(board, myPosition, -1, +2));
        //left-up
        moves.addAll(moveCalc(board, myPosition, +1, -2));
        //left-down
        moves.addAll(moveCalc(board, myPosition, -1, -2));

        return moves;
    }

    @Override
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        Collection<ChessMove> bishopMove = bishopMoves(board, myPosition);
        Collection<ChessMove> rookMove = rookMoves(board, myPosition);
        moves.addAll(bishopMove);
        moves.addAll(rookMove);
        return moves;
    }

    @Override
    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        // calculate all possible positions
        // move up
        moves.addAll(moveCalc(board, myPosition, 1, 0)); // last two arguments represent where the king would move
        // move up right
        moves.addAll(moveCalc(board, myPosition, 1, 1));
        // move up left
        moves.addAll(moveCalc(board, myPosition, 1, -1));
        // move right
        moves.addAll(moveCalc(board, myPosition, 0, 1));
        // move up left
        moves.addAll(moveCalc(board, myPosition, 0, -1));
        // move down
        moves.addAll(moveCalc(board, myPosition, -1, 0));
        // move down right
        moves.addAll(moveCalc(board, myPosition, -1, 1));
        // move down left
        moves.addAll(moveCalc(board, myPosition, -1, -1));

        return moves;
    }

    public Collection<ChessMove> moveCalc(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow = row + rowMove;
        int newCol = col + colMove;
        ChessPiece currentPiece = board.getPiece(myPosition);
        if (newRow <= 8 && newCol<= 8 && newRow >= 1 && newCol >= 1) {
            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessPiece occupant = board.getPiece(endPosition);
            if (occupant != null) {
                if (currentPiece.getTeamColor() != occupant.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, endPosition, null); // create ChessMove datatype from start position and end position
                    moves.add(move); // add ChessMove to ArrayList
                }
            }
            else {
                ChessMove move = new ChessMove(myPosition, endPosition, null); // create ChessMove datatype from start position and end position
                moves.add(move); // add ChessMove to ArrayList
            }
        }
        return moves;
    }

    public Collection<ChessMove> repeatMoveCalc(ChessBoard board, ChessPosition myPosition, int rowMove, int colMove) {
        ArrayList<ChessMove> moves = new ArrayList<>(); // initialize move ArrayList
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int rowJump = 0; // these jump variable tell the single moveCalc to know how much the piece is trying to move from its starting position
        int colJump = 0;
        while (row <= 8 && col <= 8 && row >= 1 && col >= 1) {
            // next few if statements check if the row/col is going to increase, decrease, or stay the same
            // this allows the function to be used for both bishops and rooks
            // it also increases how far from the start position a move needs to be calculated
            if (rowMove == 1) {
                row++;
                rowJump++;
            }
            if (rowMove == -1) {
                row--;
                rowJump--;
            }
            if (colMove == 1) {
                col++;
                colJump++;
            }
            if (colMove == -1) {
                col--;
                colJump--;
            }
            // if out of bounds break out of while loop
            if (row > 8 || col > 8 || row < 1 || col < 1) {
                break;
            }
            //get move from moveCalc but need to "update" the position
            ChessMove[] moveCalcArray = moveCalc(board, myPosition, rowJump, colJump).toArray(new ChessMove[0]);
            if (moveCalcArray.length > 0) {
                ChessMove move = moveCalcArray[0];
                ChessPosition endPosition = move.getEndPosition();
                ChessPiece occupant = board.getPiece(endPosition);
                if (occupant != null) {
                    if (piece.getTeamColor() != occupant.getTeamColor()) {
                        moves.add(move);
                    }
                    break;
                } else {
                    moves.add(move);
                }
            }
            else {
                break;
            }
        }

        return moves;
    }

    public Collection<ChessMove> pawnCalc(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        int startSpace;
        int promotionSpace;
        int advance;
        if (color == ChessGame.TeamColor.BLACK) {
            promotionSpace = 1;
            startSpace = 7;
            advance = -1;
        }
        else {
            promotionSpace = 8;
            startSpace = 2;
            advance = 1;
        }

        if ((color == ChessGame.TeamColor.BLACK && row > 1) || (color == ChessGame.TeamColor.WHITE && row < 8)) {
            // check if pawn can move two spaces from starting position
            if (row == startSpace) {
                ChessPosition endPosition = new ChessPosition(row + 2 * advance, col);
                ChessPosition closeOpponent = new ChessPosition(row + advance, col);
                ChessPiece occupant = board.getPiece(endPosition);
                ChessPiece closeOccupant = board.getPiece(closeOpponent);
                if (occupant == null && closeOccupant == null) {
                    ChessMove move = new ChessMove(myPosition, endPosition, null);
                    moves.add(move);
                }
            }

            // normal pawn movement
            addPawnAdvanceMove(board, moves, myPosition, row, col, advance, promotionSpace);

            // pawn capturing enemies
            addPawnCaptureMove(board, moves, myPosition, row, col, advance, col - 1, promotionSpace, color);
            addPawnCaptureMove(board, moves, myPosition, row, col, advance, col + 1, promotionSpace, color);
        }

        return moves;
    }

    private void addPawnAdvanceMove(ChessBoard board, Collection<ChessMove> moves, ChessPosition start,
                                    int row, int col, int advance, int promotionSpace) {
        ChessPosition end = new ChessPosition(row + advance, col);
        if (board.getPiece(end) == null) {
            if (row + advance == promotionSpace) {
                for (PieceType promotion : new PieceType[]{PieceType.ROOK, PieceType.QUEEN, PieceType.KNIGHT, PieceType.BISHOP}) {
                    ChessMove move = new ChessMove(start, end, promotion);
                    moves.add(move);
                }
            }
            else {
                moves.add(new ChessMove(start, end, null));
            }
        }
    }

    private void addPawnCaptureMove(ChessBoard board, Collection<ChessMove> moves, ChessPosition start,
                                    int row, int col, int advance, int newCol, int promotionSpace, ChessGame.TeamColor color) {
        if (newCol < 1 || newCol > 8) {
            return; // not a possible move
        }
        ChessPosition endPos = new ChessPosition(row + advance, newCol);
        ChessPiece occupant = board.getPiece(endPos);
        if (occupant == null || occupant.getTeamColor() == color) {
            return; // not a possible move
        }
        if (row + advance == promotionSpace) {
            for (PieceType promotion : new PieceType[]{PieceType.ROOK, PieceType.QUEEN, PieceType.KNIGHT, PieceType.BISHOP}) {
                ChessMove move = new ChessMove(start, endPos, promotion);
                moves.add(move);
            }
        }
        else {
            moves.add(new ChessMove(start, endPos, null));
        }
    }
}
