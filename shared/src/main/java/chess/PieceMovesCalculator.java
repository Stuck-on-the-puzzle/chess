package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMovesCalculator {

    // calculate pawn's moves
    public Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition myPosition);

    // calculates bishop's moves
    public Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition myPosition);

    // calculate rook's moves
    public Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition myPosition);

    // calculate knight's moves
    public Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition myPosition);

    // calculate queen's moves
    public Collection<ChessMove> QueenMoves(ChessBoard board, ChessPosition myPosition);

    // calculate king's moves
    public Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition myPosition);
}
