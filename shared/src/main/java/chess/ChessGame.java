package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ///////////////////// include new test cases from github!! ////////////////
    private TeamColor currentTeam;
    private ChessBoard gameBoard;
    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // possible implementation:
        // if the piece is a king, make sure it is not in danger
        // otherwise pieceMoves should take care of it
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);
        // if there is no piece in startPosition, return null
        if (piece == null) {
            return null;
        }
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(piece.pieceMoves(gameBoard, startPosition));

        // if king is in check, it must be protected
        if (isInCheck(currentTeam)) {
            ChessGame dummyGame = new ChessGame();
            dummyGame.setBoard(gameBoard);
        }
        // loop through moves in possible moves and add the legal ones to the validMoves collection
        for (ChessMove move: possibleMoves) {
            ChessGame dummyGame = new ChessGame();
            dummyGame.setBoard(gameBoard);
//            dummyGame.makeMove(move);
            if (!dummyGame.isInCheck(currentTeam)) { // check if move puts king in check, if not add move to collection
                moves.add(move);
            }
        }


        //


        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // updates board when it comes to capturing a piece, promoting a pawn, or just moving a piece
        // this also might update what color turn it is
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = gameBoard.getPiece(start);
        if (piece != null) {
            if (piece.getTeamColor() != currentTeam) {
                throw new InvalidMoveException("Wrong color team tried to play");
            } else if (validMoves(start).isEmpty()) {
                throw new InvalidMoveException("Invalid move");
            } else {
                gameBoard.addPiece(end, piece);
            }
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = getKingPosition(teamColor);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(pos);
                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        ChessMove[] enemyMoves = piece.pieceMoves(gameBoard, pos).toArray(new ChessMove[0]);
                        for (ChessMove move : enemyMoves) {
                            if (Objects.equals(move.getEndPosition(), kingPos)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(pos);
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        return pos;
                    }
                }
            }
        }
        throw new RuntimeException("No King Found");
    }

//    public Collection<ChessMove> getOpponentMoves(TeamColor teamColor) { // potentially useful function
//        ArrayList<ChessMove> opponentMoves = new ArrayList<>();
//        for (int i = 1; i < 9; i++) {
//            for (int j = 1; j < 9; j++) {
//                ChessPosition pos = new ChessPosition(i, j);
//                ChessPiece piece = gameBoard.getPiece(pos);
//                if (piece.getTeamColor() == teamColor) {
//                    opponentMoves.add();
//                }
//            }
//        }
//    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, gameBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentTeam=" + currentTeam +
                ", gameBoard=" + gameBoard +
                '}';
    }
}
