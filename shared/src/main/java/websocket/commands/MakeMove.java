package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID, move);
    }

}
