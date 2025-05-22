package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMovesCalculator {

    // calculate pawn's moves
    Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition);

    // calculates bishop's moves
    Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition);

    // calculate rook's moves
    Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition);

    // calculate knight's moves
    Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition);

    // calculate queen's moves
    Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition);

    // calculate king's moves
    Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition);
}
