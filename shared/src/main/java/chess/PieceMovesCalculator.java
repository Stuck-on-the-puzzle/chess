package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMovesCalculator {

    // calculate pawn's moves
    Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition myPosition);

    // calculates bishop's moves
    Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition);

    // calculate rook's moves
    Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition myPosition);

    // calculate knight's moves
    Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition myPosition);

    // calculate queen's moves
    Collection<ChessMove> QueenMoves(ChessBoard board, ChessPosition myPosition);

    // calculate king's moves
    Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition myPosition);
}
