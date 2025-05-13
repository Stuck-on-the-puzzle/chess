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
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);
        // if there is no piece in startPosition, return null
        if (piece == null) {
            return null;
        }
        // get a list of all the possible move to check legality
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(piece.pieceMoves(gameBoard, startPosition));
        // loop through moves in possible moves and see if any move leaves king in check or can protect the king
        for (ChessMove move : possibleMoves) {
            // make a dummy game/board, make the move, check if it causes checck
            ChessGame dummyGame = new ChessGame();
            dummyGame.setBoard(gameBoard.clone());
            dummyGame.setTeamTurn(piece.getTeamColor());
            ChessBoard dummyBoard = dummyGame.getBoard();
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            dummyBoard.addPiece(end, piece);
            dummyBoard.addPiece(start, null);
            // add move if check is not an issue
            if (!dummyGame.isInCheck(piece.getTeamColor())) {
                moves.add(move);
            }
        }
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
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        ChessPiece piece = gameBoard.getPiece(start);
        Collection<ChessMove> legalMoves = validMoves(start);
        if (piece != null) {
            if (piece.getTeamColor() != currentTeam) {
                throw new InvalidMoveException("Wrong color team tried to play");
            }
            else if (!legalMoves.contains(move)) {
                throw new InvalidMoveException("Invalid move");
            }
            else {
                if (promotion != null) {
                    ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), promotion);
                    gameBoard.addPiece(end, promotionPiece);
                    gameBoard.addPiece(start, null);
                }
                else {
                    gameBoard.addPiece(end, piece);
                    gameBoard.addPiece(start, null);
                }
            }
        }
        else {
            throw new InvalidMoveException("No piece in starting position");
        }

        // switch turns
        if (currentTeam == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPos = getKingPosition(teamColor);
        TeamColor enemyColor;
        if (teamColor == TeamColor.WHITE) {enemyColor = TeamColor.BLACK;}
        else {enemyColor = TeamColor.WHITE;}
        Collection<ChessPosition> teamMoves = getTeamPossibleEndPos(teamColor);
        Collection<ChessPosition> enemyMoves = getTeamPossibleEndPos(enemyColor);
        return teamMoves.isEmpty() && enemyMoves.contains(kingPos);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPos = getKingPosition(teamColor);
        TeamColor enemyColor;
        if (teamColor == TeamColor.WHITE) {enemyColor = TeamColor.BLACK;}
        else {enemyColor = TeamColor.WHITE;}
        Collection<ChessPosition> teamMoves = getTeamPossibleEndPos(teamColor);
        Collection<ChessPosition> enemyMoves = getTeamPossibleEndPos(enemyColor);
        return teamMoves.isEmpty() && !enemyMoves.contains(kingPos);
    }


    public Collection<ChessPosition> getTeamPossibleEndPos(TeamColor teamColor) {
        Collection<ChessPosition> teamPos = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(pos);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        ChessMove[] moves = validMoves(pos).toArray(new ChessMove[0]);
                        for (ChessMove move : moves) {
                            teamPos.add(move.getEndPosition());
                        }
                    }
                }
            }
        }
        return teamPos;
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

    @Override
    public ChessGame clone() {
        try {
            ChessGame clonedGame = (ChessGame) super.clone();
            // Deep copy the board
            clonedGame.gameBoard = this.gameBoard.clone();
            return clonedGame;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
