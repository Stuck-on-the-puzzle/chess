package Websocket;

import Websocket.ServerMessageObserver;
import Websocket.WebsocketCommunicator;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private final WebsocketCommunicator communicator;

    public WebSocketFacade(String serverURL, ServerMessageObserver observer) throws ResponseException {
        this.communicator = new WebsocketCommunicator(serverURL, observer);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    // helper function to send commands through websocket
    private void sendCommand(UserGameCommand command) throws IOException {
        String json = new Gson().toJson(command);
        communicator.send(json);
    }

    public void connect(String authToken, int gameID) throws IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public ChessGame makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        sendCommand(new MakeMove(authToken, gameID, move));
        return null;
    }

    public void leave(String authToken, int gameID) throws IOException {
        sendCommand(new Leave(authToken, gameID));
    }

    public void resign(String authToken, int gameID) throws IOException {
        sendCommand(new Resign(authToken, gameID));
    }




    /// Notifications:
    // 1 - User connects and message displays Player's name and team color
    // 2 - User connect as observer and display's observer's name
    // 3 - User makes a move. Display player's name and move (board updates)
    // 4 - User leaves a game. Display user's name
    // 5 - User resigns. Display user's name
    // 6 - Player is in check. Display user's name
    // 7 - Player is in checkmate. Display user's name

    /// Gameplay functionality:
    // Help
    // Redraw Chess Board
    // Leave
    // Make Move
    // Resign
    // Highlight Legal Moves
}
